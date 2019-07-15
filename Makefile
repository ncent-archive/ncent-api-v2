rand-name:=new-db-$$(date +'%Y%m%d-%H%M%S')
name?=$(rand-name)
vpc_cidr?=172.20.0.0/16
subnet1_cidr?=172.20.1.0/24
subnet2_cidr?=172.20.2.0/24
master_user_password?=$(name)

.PHONY: setup
setup:
		node env-setup.js --vpc_cidr $(vpc_cidr) --subnet1_cidr $(subnet1_cidr) --subnet2_cidr $(subnet2_cidr) --key_pair_name $(name) --security_group_name $(name) --security_group_description $(name) --db_cluster_identifier $(name) --master_username master --master_user_password $(master_user_password) --db_subnet_group_name $(name) --db_subnet_group_description $(name)