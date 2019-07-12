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
  aws.command(`ec2 create-vpc --cidr-block ${argv.vpc_cidr}`) // Create VPC
  .then(vpcResponse => {
    console.log('--Vpc CREATED ', vpcResponse.object['Vpc']['VpcId']);
    aws.command(`ec2 create-subnet \
      --availability-zone us-west-1a \
      --vpc-id ${vpcResponse.object['Vpc']['VpcId']} \
      --cidr-block ${argv.subnet1_cidr}`) // Create subnet us-west-1a
    .then(subnet1Response => {
      console.log('--Subnet us-west-1a CREATED ', subnet1Response.object['Subnet']['SubnetId']);
      aws.command(`ec2 create-subnet \
        --availability-zone us-west-1b \
        --vpc-id ${vpcResponse.object['Vpc']['VpcId']} \
        --cidr-block ${argv.subnet2_cidr}`) // Create subnet us-west-1b
      .then(subnet2Response => {
        console.log('--Subnet us-west-1b CREATED ', subnet2Response.object['Subnet']['SubnetId']);
        aws.command(`ec2 create-key-pair --key-name ${argv.key_pair_name}`) // Create key pair
        .then(keyPairResponse => {
          console.log('--KeyPair CREATED ', argv.key_pair_name);
          aws.command(`ec2 create-security-group \
            --group-name ${argv.security_group_name} \
            --description "${argv.security_group_description}" \
            --vpc-id ${vpcResponse.object['Vpc']['VpcId']}`) // Create security Group
          .then(securityGroupResponse => {
            console.log('--SecurityGroup CREATED ', argv.security_group_name);
            aws.command(`rds create-db-subnet-group \
              --db-subnet-group-name ${argv.db_subnet_group_name} \
              --db-subnet-group-description ${argv.db_subnet_group_description} \
              --subnet-ids ${subnet1Response.object['Subnet']['SubnetId']} ${subnet2Response.object['Subnet']['SubnetId']}`)
            .then(dbSubnetGroupResponse => {
              console.log('--DBSubnetGroup CREATED ', argv.db_subnet_group_name);
              aws.command(`rds create-db-cluster \
                --availability-zones us-west-1a \
                --db-cluster-identifier ${argv.db_cluster_identifier} \
                --engine aurora-mysql \
                --engine-version 5.7.12 \
                --master-username ${argv.master_username} \
                --master-user-password ${argv.master_user_password} \
                --db-subnet-group-name ${dbSubnetGroupResponse.object['DBSubnetGroup']['DBSubnetGroupName']} \
                --vpc-security-group-ids ${securityGroupResponse.object['GroupId']}`) // Create Aurora DB Cluster
              .then(dbClusterResponse=>{
                console.log('--DBCluster CREATED ', argv.db_cluster_identifier);
                aws.command(`ec2 run-instances \
                  --image-id ami-056ee704806822732 \
                  --count 1 \
                  --instance-type t2.micro \
                  --placement AvailabilityZone=us-west-1a \
                  --key-name ${argv.key_pair_name} \
                  --security-group-ids ${securityGroupResponse.object['GroupId']} \
                  --subnet-id ${subnet1Response.object['Subnet']['SubnetId']}`)
                .then(instanceResponse => {
                  console.log('--EC2 CREATED ',instanceResponse.object['Instances'][0]['InstanceId']);
                  console.log('--ALL DONE--');
                });
              });
            });
          });
        });
      });
    });
  });
}