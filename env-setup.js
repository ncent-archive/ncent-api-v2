const argv = require('minimist')(process.argv.slice(2));
const awsCli = require('aws-cli-js');
const Options = awsCli.Options;
const Aws = awsCli.Aws;

const options = new Options();

const aws = new Aws(options);

if (!(argv.vpc_cidr && 
    argv.subnet1_cidr && 
    argv.subnet2_cidr &&
    argv.key_pair_name && 
    argv.security_group_name && 
    argv.security_group_description && 
    argv.db_cluster_identifier &&
    argv.master_username && 
    argv.master_user_password &&
    argv.db_subnet_group_name &&
    argv.db_subnet_group_description
  )) {
 
    console.error('Command requires the following arguments:\n --vpc_cidr\n --subnet_cidr\n --subnet2_cidr\n --key_pair_name\n --security_group_name\n --security_group_description\n --db_cluster_identifier\n --master_username\n --master_user_password\n --db_subnet_group_name\n --db_subnet_group_description\n')

} else {
  const run = async () => {
    // Create VPC
    const vpcResponse = await aws.command(`ec2 create-vpc --cidr-block ${argv.vpc_cidr}`);
    console.log('--Vpc CREATED ', vpcResponse.object['Vpc']['VpcId']);

    // Enable DNS Support
    const enableDNSSupportResponse = await aws.command(`ec2 modify-vpc-attribute \
      --vpc-id ${vpcResponse.object['Vpc']['VpcId']} \
      --enable-dns-support "{\\"Value\\":true}"`)
    console.log('--Vpc DNS ENABLED ', vpcResponse.object['Vpc']['VpcId']);

    // Enable DNS Hostname
    const enableDNSHostnameResponse = await aws.command(`ec2 modify-vpc-attribute \
      --vpc-id ${vpcResponse.object['Vpc']['VpcId']} \
      --enable-dns-hostnames "{\\"Value\\":true}"`)
    console.log('--Vpc DNS Hostname ENABLED ', vpcResponse.object['Vpc']['VpcId']);

    // Create subnet us-west-1a
    const subnet1Response = await aws.command(`ec2 create-subnet \
      --availability-zone us-west-1a \
      --vpc-id ${vpcResponse.object['Vpc']['VpcId']} \
      --cidr-block ${argv.subnet1_cidr}`);
    console.log('--Subnet us-west-1a CREATED ', subnet1Response.object['Subnet']['SubnetId']);

    // Enable Subnet 1 Public IP
    const subnet1PublicIPResponse = await aws.command(`ec2 modify-subnet-attribute \
      --subnet-id ${subnet1Response.object['Subnet']['SubnetId']} \
      --map-public-ip-on-launch`);
    console.log('--Subnet us-west-1a Public IP ENABLED ', subnet1Response.object['Subnet']['SubnetId']);

    // Create subnet us-west-1b
    const subnet2Response = await aws.command(`ec2 create-subnet \
      --availability-zone us-west-1b \
      --vpc-id ${vpcResponse.object['Vpc']['VpcId']} \
      --cidr-block ${argv.subnet2_cidr}`)
    console.log('--Subnet us-west-1b CREATED ', subnet2Response.object['Subnet']['SubnetId']);

    // Enable Subnet 2 Public IP
    const subnet2PublicIPResponse = await aws.command(`ec2 modify-subnet-attribute \
      --subnet-id ${subnet2Response.object['Subnet']['SubnetId']} \
      --map-public-ip-on-launch`);
    console.log('--Subnet us-west-1b Public IP ENABLED ', subnet2Response.object['Subnet']['SubnetId']);

    // Create key pair
    const keyPairResponse = await aws.command(`ec2 create-key-pair --key-name ${argv.key_pair_name}`);
    console.log('--KeyPair CREATED ', argv.key_pair_name);

    // Create security Group
    const securityGroupResponse = await  aws.command(`ec2 create-security-group \
      --group-name ${argv.security_group_name} \
      --description "${argv.security_group_description}" \
      --vpc-id ${vpcResponse.object['Vpc']['VpcId']}`);
    console.log('--SecurityGroup CREATED ', argv.security_group_name);

    // Create SSH access rule
    const sshAccessRuleResponse = await aws.command(`ec2 authorize-security-group-ingress \
      --group-id ${securityGroupResponse.object['GroupId']} \
      --protocol tcp \
      --port 22 \
      --cidr 0.0.0.0/0`);
    console.log('--SecurityGroup SSH rule CREATED ', argv.security_group_name);

    // Create MYSQL/Aurora access rule
    const auroraAccessRuleResponse = await  aws.command(`ec2 authorize-security-group-ingress \
      --group-id ${securityGroupResponse.object['GroupId']} \
      --protocol tcp \
      --port 3306 \
      --cidr 0.0.0.0/0`);
    console.log('--SecurityGroup MYSQL/Aurora rule CREATED ', argv.security_group_name);

    // Create DB Subnet Group
    const dbSubnetGroupResponse = await aws.command(`rds create-db-subnet-group \
      --db-subnet-group-name ${argv.db_subnet_group_name} \
      --db-subnet-group-description ${argv.db_subnet_group_description} \
      --subnet-ids ${subnet1Response.object['Subnet']['SubnetId']} ${subnet2Response.object['Subnet']['SubnetId']}`);
    console.log('--DBSubnetGroup CREATED ', argv.db_subnet_group_name);

    // Create Aurora DB Cluster
    const dbClusterResponse = await aws.command(`rds create-db-cluster \
      --availability-zones us-west-1a \
      --db-cluster-identifier ${argv.db_cluster_identifier} \
      --engine aurora-mysql \
      --engine-version 5.7.12 \
      --master-username ${argv.master_username} \
      --master-user-password ${argv.master_user_password} \
      --db-subnet-group-name ${dbSubnetGroupResponse.object['DBSubnetGroup']['DBSubnetGroupName']} \
      --vpc-security-group-ids ${securityGroupResponse.object['GroupId']}`);
    console.log('--DBCluster CREATED ', argv.db_cluster_identifier);

    // Create EC2 Instance
    const instanceResponse = await aws.command(`ec2 run-instances \
      --image-id ami-056ee704806822732 \
      --count 1 \
      --instance-type t2.micro \
      --placement AvailabilityZone=us-west-1a \
      --key-name ${argv.key_pair_name} \
      --security-group-ids ${securityGroupResponse.object['GroupId']} \
      --subnet-id ${subnet1Response.object['Subnet']['SubnetId']}`);
    console.log('--EC2 CREATED ',instanceResponse.object['Instances'][0]['InstanceId']);

    console.log('--ALL DONE--');
  }

  run();
}