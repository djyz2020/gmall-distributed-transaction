全球小店-分布式事务
## 一、 Nacos和Seata高可用部署
### 1. mysql主从库创建nacos相关数据库表
```
/******************************************/
/*   数据库全名 = nacos   */
/*   表名称 = config_info   */
/******************************************/
CREATE TABLE `config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) DEFAULT NULL,
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) DEFAULT NULL,
  `c_use` varchar(64) DEFAULT NULL,
  `effect` varchar(64) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `c_schema` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

/******************************************/
/*   数据库全名 = nacos   */
/*   表名称 = config_info_aggr   */
/******************************************/
CREATE TABLE `config_info_aggr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) NOT NULL COMMENT 'datum_id',
  `content` longtext NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='增加租户字段';


/******************************************/
/*   数据库全名 = nacos   */
/*   表名称 = config_info_beta   */
/******************************************/
CREATE TABLE `config_info_beta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_beta';

/******************************************/
/*   数据库全名 = nacos   */
/*   表名称 = config_info_tag   */
/******************************************/
CREATE TABLE `config_info_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_tag';

/******************************************/
/*   数据库全名 = nacos   */
/*   表名称 = config_tags_relation   */
/******************************************/
CREATE TABLE `config_tags_relation` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`),
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

/******************************************/
/*   数据库全名 = nacos   */
/*   表名称 = group_capacity   */
/******************************************/
CREATE TABLE `group_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='集群、各Group容量信息表';

/******************************************/
/*   数据库全名 = nacos   */
/*   表名称 = his_config_info   */
/******************************************/
CREATE TABLE `his_config_info` (
  `id` bigint(64) unsigned NOT NULL,
  `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL,
  `md5` varchar(32) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text,
  `src_ip` varchar(50) DEFAULT NULL,
  `op_type` char(10) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='多租户改造';


/******************************************/
/*   数据库全名 = nacos   */
/*   表名称 = tenant_capacity   */
/******************************************/
CREATE TABLE `tenant_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='租户容量信息表';


CREATE TABLE `tenant_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) default '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) default '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

CREATE TABLE `users` (
    `username` varchar(50) NOT NULL PRIMARY KEY,
    `password` varchar(500) NOT NULL,
    `enabled` boolean NOT NULL
);

CREATE TABLE `roles` (
    `username` varchar(50) NOT NULL,
    `role` varchar(50) NOT NULL,
    UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
);

CREATE TABLE `permissions` (
    `role` varchar(50) NOT NULL,
    `resource` varchar(255) NOT NULL,
    `action` varchar(8) NOT NULL,
    UNIQUE INDEX `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
);

INSERT INTO users (username, password, enabled) VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);

INSERT INTO roles (username, role) VALUES ('nacos', 'ROLE_ADMIN');
```
-----------------------------------------------------------------------------------------------------------------------------------------
### 2. Nacos集群部署
Docker集群部署Nacos时要注意，创建的Nacos容器填写的ip都是外部ip，但是创建的网段却是docker内部的网段，因此，我们需要创建在同一网段的ip。

2.1 创建自定义网络
```
docker network ls
docker network create --driver bridge --subnet=172.19.0.0/16 --gateway=172.19.0.1 nacos-network
docker network inspect nacos-network
```
2.2 创建nacos集群
```
docker run -itd \
-e PREFER_HOST_MODE=ip \
-e MODE=cluster \
-e NACOS_SERVER_IP=172.19.0.2 \
-e NACOS_SERVERS="172.19.0.2:8848 172.19.0.3:8848 172.19.0.4:8848" \
-e SPRING_DATASOURCE_PLATFORM=mysql \
-e MYSQL_SERVICE_HOST=192.168.126.137 \
-e MYSQL_SERVICE_PORT=3310 \
-e MYSQL_SERVICE_DB_NAME=nacos \
-e MYSQL_SERVICE_USER=root \
-e MYSQL_SERVICE_PASSWORD=123456 \
-p 8848:8848 \
--network nacos-network \
--name nacos-8848 \
-p 9848:9848 \
-p 9849:9849 \
--ip=172.19.0.2 \
--restart=always \
nacos/nacos-server

docker run -itd \
-e PREFER_HOST_MODE=ip \
-e MODE=cluster \
-e NACOS_SERVER_IP=172.19.0.3 \
-e NACOS_SERVERS="172.19.0.2:8848 172.19.0.3:8848 172.19.0.4:8848" \
-e SPRING_DATASOURCE_PLATFORM=mysql \
-e MYSQL_SERVICE_HOST=192.168.126.137 \
-e MYSQL_SERVICE_PORT=3310 \
-e MYSQL_SERVICE_DB_NAME=nacos \
-e MYSQL_SERVICE_USER=root \
-e MYSQL_SERVICE_PASSWORD=123456 \
-p 8849:8848 \
-p 9858:9848 \
-p 9859:9849 \
--network nacos-network \
--name nacos-8849 \
--ip=172.19.0.3 \
--restart=always \
nacos/nacos-server

docker run -itd \
-e PREFER_HOST_MODE=ip \
-e MODE=cluster \
-e NACOS_SERVER_IP=172.19.0.4 \
-e NACOS_SERVERS="172.19.0.2:8848 172.19.0.3:8848 172.19.0.4:8848" \
-e SPRING_DATASOURCE_PLATFORM=mysql \
-e MYSQL_SERVICE_HOST=192.168.126.137 \
-e MYSQL_SERVICE_PORT=3310 \
-e MYSQL_SERVICE_DB_NAME=nacos \
-e MYSQL_SERVICE_USER=root \
-e MYSQL_SERVICE_PASSWORD=123456 \
-p 8850:8848 \
-p 9868:9848 \
-p 9869:9849 \
--network nacos-network \
--name nacos-8850 \
--ip=172.19.0.4 \
--restart=always \
nacos/nacos-server
```
2.3 访问nacos集群
```
http://[IP]:[PORT]/nacos/index.html
```
-----------------------------------------------------------------------------------------------------------------------------------------

### 3. 使用Nginx反向代理Nacos集群
3.1 拉取nginx镜像
```
docker pull nginx
```
3.2 创建nginx容器
```
mkdir -p /opt/nginx/conf && cd /opt/nginx/conf
vim nginx.conf 
-------------------------------------------------------------------------------
user  nginx;
worker_processes  auto;
error_log  /var/log/nginx/error.log notice;
pid        /var/run/nginx.pid;
events {
    worker_connections  1024;
}
http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log  main;
    sendfile on;
    keepalive_timeout 65;
    gzip  on;
    include /etc/nginx/conf.d/*.conf;
    include /etc/nginx/conf.d/*.conf;
    upstream nacos{
        server 172.19.0.2:8848;
        server 172.19.0.3:8848;
        server 172.19.0.4:8848;
    }
    server {
        listen 8080;
        server_name nacos-nginx;
        location / {
            proxy_pass http://nacos;
        }
    }
}
-------------------------------------------------------------------------------
docker run -itd \
--name nginx \
--network=nacos-network \
--ip=172.19.0.5 \
--restart=always \
-v /opt/nginx/conf/nginx.conf:/etc/nginx/nginx.conf \
-p 8080:8080 \
-d nginx
```

为保证用户敏感配置数据的安全，Nacos 提供了配置加密的新特性。降低了用户使用的风险，也不需要再对配置进行单独的加密处理。
数据库表 config_info、config_info_beta、his_config_info中需要新增字段 encrypted_data_key ，用来存储每一个配置项加密使用的秘钥。新版本的默认创建表的sql中已经添加该字段。
--对于目前已经搭建好的 Nacos 使用以下 sql 将字段添加到对应的表中：
```
ALTER TABLE config_info ADD COLUMN `encrypted_data_key` text NOT NULL COMMENT '秘钥';
ALTER TABLE config_info_beta ADD COLUMN `encrypted_data_key` text NOT NULL COMMENT '秘钥';
ALTER TABLE his_config_info ADD COLUMN `encrypted_data_key` text NOT NULL COMMENT '秘钥';
```
### 4. seata组件docker高可用部署
4.1 配置初始化 <br/>
a) mysql数据库初始化，创建seata数据库，并执行以下脚本：
```
-- the table to store GlobalSession data
CREATE TABLE IF NOT EXISTS `global_table`
(
    `xid`                       VARCHAR(128) NOT NULL,
    `transaction_id`            BIGINT,
    `status`                    TINYINT      NOT NULL,
    `application_id`            VARCHAR(32),
    `transaction_service_group` VARCHAR(32),
    `transaction_name`          VARCHAR(128),
    `timeout`                   INT,
    `begin_time`                BIGINT,
    `application_data`          VARCHAR(2000),
    `gmt_create`                DATETIME,
    `gmt_modified`              DATETIME,
    PRIMARY KEY (`xid`),
    KEY `idx_status_gmt_modified` (`status` , `gmt_modified`),
    KEY `idx_transaction_id` (`transaction_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- the table to store BranchSession data
CREATE TABLE IF NOT EXISTS `branch_table`
(
    `branch_id`         BIGINT       NOT NULL,
    `xid`               VARCHAR(128) NOT NULL,
    `transaction_id`    BIGINT,
    `resource_group_id` VARCHAR(32),
    `resource_id`       VARCHAR(256),
    `branch_type`       VARCHAR(8),
    `status`            TINYINT,
    `client_id`         VARCHAR(64),
    `application_data`  VARCHAR(2000),
    `gmt_create`        DATETIME(6),
    `gmt_modified`      DATETIME(6),
    PRIMARY KEY (`branch_id`),
    KEY `idx_xid` (`xid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- the table to store lock data
CREATE TABLE IF NOT EXISTS `lock_table`
(
    `row_key`        VARCHAR(128) NOT NULL,
    `xid`            VARCHAR(128),
    `transaction_id` BIGINT,
    `branch_id`      BIGINT       NOT NULL,
    `resource_id`    VARCHAR(256),
    `table_name`     VARCHAR(32),
    `pk`             VARCHAR(36),
    `status`         TINYINT      NOT NULL DEFAULT '0' COMMENT '0:locked ,1:rollbacking',
    `gmt_create`     DATETIME,
    `gmt_modified`   DATETIME,
    PRIMARY KEY (`row_key`),
    KEY `idx_status` (`status`),
    KEY `idx_branch_id` (`branch_id`),
    KEY `idx_xid` (`xid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `distributed_lock`
(
    `lock_key`       CHAR(20) NOT NULL,
    `lock_value`     VARCHAR(20) NOT NULL,
    `expire`         BIGINT,
    primary key (`lock_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('AsyncCommitting', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('RetryCommitting', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('RetryRollbacking', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('TxTimeoutCheck', ' ', 0);
```  
b) nacos配置初始化 <br/>
创建文件nacos-config.sh
```
#!/bin/sh
# Copyright 1999-2019 Seata.io Group.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at、
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

while getopts ":h:p:g:t:u:w:" opt
do
  case $opt in
  h)
    host=$OPTARG
    ;;
  p)
    port=$OPTARG
    ;;
  g)
    group=$OPTARG
    ;;
  t)
    tenant=$OPTARG
    ;;
  u)
    username=$OPTARG
    ;;
  w)
    password=$OPTARG
    ;;
  ?)
    echo " USAGE OPTION: $0 [-h host] [-p port] [-g group] [-t tenant] [-u username] [-w password] "
    exit 1
    ;;
  esac
done

if [ -z ${host} ]; then
    host=localhost
fi
if [ -z ${port} ]; then
    port=8848
fi
if [ -z ${group} ]; then
    group="SEATA_GROUP"
fi
if [ -z ${tenant} ]; then
    tenant=""
fi
if [ -z ${username} ]; then
    username=""
fi
if [ -z ${password} ]; then
    password=""
fi

nacosAddr=$host:$port
contentType="content-type:application/json;charset=UTF-8"

echo "set nacosAddr=$nacosAddr"
echo "set group=$group"

urlencode() {
  length="${#1}"
  i=0
  while [ $length -gt $i ]; do
    char="${1:$i:1}"
    case $char in
    [a-zA-Z0-9.~_-]) printf $char ;;
    *) printf '%%%02X' "'$char" ;;
    esac
    i=`expr $i + 1`
  done
}

failCount=0
tempLog=$(mktemp -u)
function addConfig() {
  dataId=`urlencode $1`
  content=`urlencode $2`
  curl -X POST -H "${contentType}" "http://$nacosAddr/nacos/v1/cs/configs?dataId=$dataId&group=$group&content=$content&tenant=$tenant&username=$username&password=$password" >"${tempLog}" 2>/dev/null
  if [ -z $(cat "${tempLog}") ]; then
    echo " Please check the cluster status. "
    exit 1
  fi
  if [ "$(cat "${tempLog}")" == "true" ]; then
    echo "Set $1=$2 successfully "
  else
    echo "Set $1=$2 failure "
    failCount=`expr $failCount + 1`
  fi
}

count=0
COMMENT_START="#"
for line in $(cat ./config.txt | sed s/[[:space:]]//g); do
    if [[ "$line" =~ ^"${COMMENT_START}".*  ]]; then
      continue
    fi
    count=`expr $count + 1`
	  key=${line%%=*}
    value=${line#*=}
	  addConfig "${key}" "${value}"
done

echo "========================================================================="
echo " Complete initialization parameters,  total-count:$count ,  failure-count:$failCount "
echo "========================================================================="

if [ ${failCount} -eq 0 ]; then
	echo " Init nacos config finished, please start seata-server. "
else
	echo " init nacos config fail. "
fi

```

创建文件config.txt
```
transport.type=TCP
transport.server=NIO
transport.heartbeat=true
transport.enableClientBatchSendRequest=false
transport.threadFactory.bossThreadPrefix=NettyBoss
transport.threadFactory.workerThreadPrefix=NettyServerNIOWorker
transport.threadFactory.serverExecutorThreadPrefix=NettyServerBizHandler
transport.threadFactory.shareBossWorker=false
transport.threadFactory.clientSelectorThreadPrefix=NettyClientSelector
transport.threadFactory.clientSelectorThreadSize=1
transport.threadFactory.clientWorkerThreadPrefix=NettyClientWorkerThread
transport.threadFactory.bossThreadSize=1
transport.threadFactory.workerThreadSize=default
transport.shutdown.wait=3
service.vgroupMapping.gmall_tx_group=default
service.default.grouplist=192.168.126.137:8091
service.enableDegrade=false
service.disableGlobalTransaction=false
client.rm.asyncCommitBufferLimit=10000
client.rm.lock.retryInterval=10
client.rm.lock.retryTimes=30
client.rm.lock.retryPolicyBranchRollbackOnConflict=true
client.rm.reportRetryCount=5
client.rm.tableMetaCheckEnable=false
client.rm.tableMetaCheckerInterval=60000
client.rm.sqlParserType=druid
client.rm.reportSuccessEnable=false
client.rm.sagaBranchRegisterEnable=false
client.tm.commitRetryCount=5
client.tm.rollbackRetryCount=5
client.tm.defaultGlobalTransactionTimeout=60000
client.tm.degradeCheck=false
client.tm.degradeCheckAllowTimes=10
client.tm.degradeCheckPeriod=2000
store.mode=db
store.db.datasource=druid
store.db.dbType=mysql
store.db.driverClassName=com.mysql.cj.jdbc.Driver
store.db.url=jdbc:mysql://192.168.126.137:3310/seata?useUnicode=true&rewriteBatchedStatements=true
store.db.user=root
store.db.password=123456
store.db.minConn=5
store.db.maxConn=30
store.db.globalTable=global_table
store.db.branchTable=branch_table
store.db.queryLimit=100
store.db.lockTable=lock_table
store.db.distributedLockTable=distributed_lock
store.db.maxWait=5000
server.recovery.committingRetryPeriod=1000
server.recovery.asynCommittingRetryPeriod=1000
server.recovery.rollbackingRetryPeriod=1000
server.recovery.timeoutRetryPeriod=1000
server.maxCommitRetryTimeout=-1
server.maxRollbackRetryTimeout=-1
server.rollbackRetryTimeoutUnlockEnable=false
client.undo.dataValidation=true
client.undo.logSerialization=jackson
client.undo.onlyCareUpdateColumns=true
server.undo.logSaveDays=7
server.undo.logDeletePeriod=86400000
client.undo.logTable=undo_log
client.undo.compress.enable=true
client.undo.compress.type=zip
client.undo.compress.threshold=64k
log.exceptionRate=100
transport.serialization=seata
transport.compressor=none
metrics.enabled=false
metrics.registryType=compact
metrics.exporterList=prometheus
metrics.exporterPrometheusPort=9898
```
执行nacos-config.sh
```
chmod +x nacos-config.sh
sh nacos-config.sh
```

4.2 注册中心和配置中心使用nacos
```
registry {
  type = "nacos"

  nacos {
    serverAddr = "192.168.126.137"
    namespace = ""
    cluster = "default"
    username = "nacos"
    password = "nacos"
  }
}

config {
  type = "nacos"
  nacos {
    application = "seata-server"
    serverAddr = "192.168.126.137"
    namespace = ""
    cluster = "default"
    username = "nacos"
    password = "nacos"
  }
}
```
4.3 存储使用mysql
```
store {
  mode = "db"
  db {
    datasource = "druid"
    dbType = "mysql"
    driverClassName = "com.mysql.cj.jdbc.Driver"
    url = "jdbc:mysql://192.168.126.137:3310/seata"
    user = "root"
    password = "123456"
    min-conn = 5
    max-conn = 30
    global.table = "global_table"
    branch.table = "branch_table"
    lock.table = "lock_table"
    queryLimit = 100
    maxWait = 5000
  }
}
```
4.4 高可用部署seata
a) 启动seata
```
docker run -itd \
--name seata-1 \
--restart=always \
-p 8091:8091 \
-p 7091:7091 \
-e SEATA_IP=192.168.126.137 \
-e SEATA_PORT=8091 \
seataio/seata-server:1.5.2
```
b) 复制配置到本地查看并修改
```
docker cp seata-1:/seata-server/resources .
```

c) 修改完后用挂载的方式启动seata
```
docker run -itd \
--name seata-1 \
--restart=always \
--ip=172.19.0.6 \
-p 8091:8091 \
-p 7091:7091 \
-e SEATA_IP=192.168.126.137 \
-e SEATA_PORT=8091 \
-e SERVER_NODE=1 \
-e STORE_MODE=db \
-v /opt/seata/application.yml:/seata-server/resources/application.yml  \
-v /opt/seata/logs:/opt/seata/logs \
seataio/seata-server:1.5.2

docker run -itd \
--name seata-2 \
--restart=always \
--ip=172.19.0.7 \
-p 8092:8091 \
-p 7092:7091 \
-e SEATA_IP=192.168.126.137 \
-e SEATA_PORT=8092 \
-e SERVER_NODE=2 \
-e STORE_MODE=db \
-v /opt/seata/application.yml:/seata-server/resources/application.yml  \
-v /opt/seata/logs:/opt/seata/logs \
seataio/seata-server:1.5.2

docker run -itd \
--name seata-3 \
--restart=always \
--ip=172.19.0.8 \
-p 8093:8091 \
-p 7093:7091 \
-e SEATA_IP=192.168.126.137 \
-e SEATA_PORT=8093 \
-e SERVER_NODE=3 \
-e STORE_MODE=db \
-v /opt/seata/application.yml:/seata-server/resources/application.yml  \
-v /opt/seata/logs:/opt/seata/logs \
seataio/seata-server:1.5.2
```
4.4 修改nginx反向代理并重新部署
```
vim nginx.conf 
-------------------------------------------------------------------------------
user  nginx;
worker_processes  auto;
error_log  /var/log/nginx/error.log notice;
pid        /var/run/nginx.pid;
events {
    worker_connections  1024;
}
stream {
    upstream nacos-9848 {
        server 192.168.126.137:9848;
        server 192.168.126.137:9858;
	    server 192.168.126.137:9868;
    }
    upstream nacos-9849 {
        server 192.168.126.137:9849;
        server 192.168.126.137:9859;
        server 192.168.126.137:9869;
    }
    server {
        listen 9848;
        proxy_pass nacos-9848;
    }
    server {
        listen 9849;
        proxy_pass nacos-9849;
    }
}
http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log  main;
    sendfile on;
    tcp_nopush on;
    keepalive_timeout 65;
    gzip  on;
    include /etc/nginx/conf.d/*.conf;
    include /etc/nginx/conf.d/*.conf;
    upstream nacos{
        server 172.19.0.2:8848;
        server 172.19.0.3:8848;
		server 172.19.0.4:8848;
    }
	upstream seata{
        server 192.168.126.137:7091;
        server 192.168.126.137:7092;
		server 192.168.126.137:7093;
    }
	# seata高可用代理
	server {
        listen 8088;
        server_name 192.168.126.137;
        location / {
            proxy_pass http://seata;
        }
    }
	# nacos高可用代理
    server {
        listen 8080;
        server_name 192.168.126.137;
        location /nacos {
            proxy_pass http://nacos;
        }
    }
}
-------------------------------------------------------------------------------
docker run -itd \
--name nginx \
--network=nacos-network \
--ip=172.19.0.5 \
--restart=always \
-v /opt/nginx/conf/nginx.conf:/etc/nginx/nginx.conf \
-p 8080:8080 \
-p 8088:8088 \
-d nginx
```

### 参考博客：
<ul>
    <li><a href="https://blog.csdn.net/weixin_43970098/article/details/126092175">使用Docker搭建Nacos集群部署微服务</a></li>
    <li><a href="https://blog.csdn.net/qq_38363828/article/details/123624344">docker部署seata</a></li>
    <li><a href="https://www.iocoder.cn/Seata/install/?self">芋道 Seata 极简入门</a></li>
</ul>

## 二、分布式事务总结

## 1.简介

> 本文主要介绍SpringBoot2.6.11 + Dubbo 3.1.1 + Mybatis 3.4.6 + Nacos 2.0.4 + Seata 1.5.2整合来实现Dubbo分布式事务管理，使用Nacos作为Dubbo和Seata的注册中心和配置中心，使用MySQL数据库和MyBatis来操作数据。

如果你还对`SpringBoot`、`Dubbo`、`Nacos`、`Seata`、` Mybatis` 不是很了解的话，这里我为大家整理个它们的官网网站，如下

- SpringBoot：[https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)

- Dubbo：[https://dubbo.apache.org/zh/index.html](https://dubbo.apache.org/zh/index.html)

- Nacos：[https://nacos.io/zh-cn/docs/quick-start.html](https://nacos.io/zh-cn/docs/quick-start.html)

- Seata：[https://seata.io/zh-cn/](https://seata.io/zh-cn/)

- MyBatis：[http://www.mybatis.org/mybatis-3/zh/index.html](http://www.mybatis.org/mybatis-3/zh/index.html)

在这里我们就不一个一个介绍它们是怎么使用和原理，详细请学习官方文档，在这里我将开始对它们进行整合，完成一个简单的案例，来让大家了解`Seata`来实现`Dubbo`分布式事务管理的基本流程。

## 2.环境准备

## 2.1 下载nacos并安装启动

nacos下载：[https://github.com/alibaba/nacos/releases/tag/1.5.2](https://github.com/alibaba/nacos/releases/tag/1.5.2)

Nacos 快速入门：[https://nacos.io/en-us/docs/quick-start.html](https://nacos.io/en-us/docs/quick-start.html)

```shell
sh startup.sh -m standalone
```

在浏览器打开Nacos web 控制台：http://192.168.126.137:8848/nacos/index.html

输入nacos的账号和密码 分别为`nacos：nacos`

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190905101221566.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9saWRvbmcxNjY1LmJsb2cuY3Nkbi5uZXQ=,size_16,color_FFFFFF,t_70)
这是时候naocs 就正常启动了。

## 2.2 下载seata server 并安装启动

#### 2.2.1 在 [Seata Release](https://github.com/seata/seata/releases) 下载最新版的 Seata Server 并解压得到如下目录：

```shell
.
├──bin
├──conf
├──file_store
└──lib
```

#### 2.2.2 修改 conf/registry.conf 配置，

目前seata支持如下的file、nacos 、apollo、zk、consul的注册中心和配置中心。这里我们以`nacos` 为例。 将 type 改为 nacos

```bash
registry {
  # file nacos
  type = "nacos"

  nacos {
    serverAddr = "192.168.126.137:8848"
    namespace = ""
    cluster = "default"
  }
  file {
    name = "file.conf"
  }
}

config {
  # file、nacos 、apollo、zk、consul
  type = "nacos"

  nacos {
    serverAddr = "192.168.126.137:8848"
    namespace = ""
  }

  file {
    name = "file.conf"
  }
}
```

- serverAddr = "192.168.126.137:8848"   ：nacos 的地址
- namespace = "" ：nacos的命名空间默认为``
- cluster = "default"  ：集群设置未默认 `default`

**注意： seata0.9.0之后，配置如下，其中`namespace = ""`**

#### 2.2.3 修改 conf/nacos-config.txt配置

```
transport.type=TCP
transport.server=NIO
transport.heartbeat=true
transport.thread-factory.boss-thread-prefix=NettyBoss
transport.thread-factory.worker-thread-prefix=NettyServerNIOWorker
transport.thread-factory.server-executor-thread-prefix=NettyServerBizHandler
transport.thread-factory.share-boss-worker=false
transport.thread-factory.client-selector-thread-prefix=NettyClientSelector
transport.thread-factory.client-selector-thread-size=1
transport.thread-factory.client-worker-thread-prefix=NettyClientWorkerThread
transport.thread-factory.boss-thread-size=1
transport.thread-factory.worker-thread-size=8
transport.shutdown.wait=3
service.vgroup_mapping.order-service-seata-service-group=default
service.vgroup_mapping.account-service-seata-service-group=default
service.vgroup_mapping.stock-service-seata-service-group=default
service.vgroup_mapping.business-service-seata-service-group=default
service.enableDegrade=false
service.disable=false
service.max.commit.retry.timeout=-1
service.max.rollback.retry.timeout=-1
client.async.commit.buffer.limit=10000
client.lock.retry.internal=10
client.lock.retry.times=30
store.mode=db
store.file.dir=file_store/data
store.file.max-branch-session-size=16384
store.file.max-global-session-size=512
store.file.file-write-buffer-cache-size=16384
store.file.flush-disk-mode=async
store.file.session.reload.read_size=100
store.db.driver-class-name=com.mysql.jdbc.Driver
store.db.datasource=dbcp
store.db.db-type=mysql
store.db.url=jdbc:mysql://192.168.126.137:3306/seata?useUnicode=true
store.db.user=root
store.db.password=123456
store.db.min-conn=1
store.db.max-conn=3
store.db.global.table=global_table
store.db.branch.table=branch_table
store.db.query-limit=100
store.db.lock-table=lock_table
recovery.committing-retry-period=1000
recovery.asyn-committing-retry-period=1000
recovery.rollbacking-retry-period=1000
recovery.timeout-retry-period=1000
transaction.undo.data.validation=true
transaction.undo.log.serialization=jackson
transaction.undo.log.save.days=7
transaction.undo.log.delete.period=86400000
transaction.undo.log.table=undo_log
transport.serialization=seata
transport.compressor=none
metrics.enabled=false
metrics.registry-type=compact
metrics.exporter-list=prometheus
metrics.exporter-prometheus-port=9898
client.report.retry.count=5
service.disableGlobalTransaction=false
client.support.spring.datasource.autoproxy=true
```

配置的详细说明参考官网：[https://seata.io/zh-cn/docs/user/configurations.html](https://seata.io/zh-cn/docs/user/configurations.html)

这里主要修改了如下几项：

- store.mode :存储模式 默认file 这里我修改为db 模式 ，并且需要三个表`global_table`、`branch_table`和`lock_table`
- store.db.driver-class-name： 0.8.0版本默认没有，会报错。添加了 `com.mysql.cj.jdbc.Driver`
- store.db.datasource=dbcp ：数据源 dbcp
- store.db.db-type=mysql : 存储数据库的类型为`mysql`
- store.db.url=jdbc:mysql://192.168.126.137:3306/seata?useUnicode=true : 修改为自己的数据库`url`、`port`、`数据库名称`
- store.db.user=root :数据库的账号
- store.db.password=123456 :数据库的密码
- service.vgroup_mapping.order-service-seata-service-group=default
- service.vgroup_mapping.account-service-seata-service-group=default
- service.vgroup_mapping.stock-service-seata-service-group=default
- service.vgroup_mapping.business-service-seata-service-group=default
- client.support.spring.datasource.autoproxy=true 开启数据源自动代理

也可以在 Nacos 配置页面添加，data-id 为 service.vgroup_mapping.${YOUR_SERVICE_NAME}-seata-service-group, group 为 SEATA_GROUP，
如果不添加该配置，启动后会提示no available server to connect

**注意：** 配置文件末尾有空行，需要删除，否则会提示失败，尽管实际上是成功的

***db模式下的所需的三个表的数据库脚本位于`seata\conf\db_store.sql`***

`global_table`的表结构

```sql
CREATE TABLE `global_table` (
  `xid` varchar(128) NOT NULL,
  `transaction_id` bigint(20) DEFAULT NULL,
  `status` tinyint(4) NOT NULL,
  `application_id` varchar(64) DEFAULT NULL,
  `transaction_service_group` varchar(64) DEFAULT NULL,
  `transaction_name` varchar(64) DEFAULT NULL,
  `timeout` int(11) DEFAULT NULL,
  `begin_time` bigint(20) DEFAULT NULL,
  `application_data` varchar(2000) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`xid`),
  KEY `idx_gmt_modified_status` (`gmt_modified`,`status`),
  KEY `idx_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

```

`branch_table`的表结构

```sql
CREATE TABLE `branch_table` (
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(128) NOT NULL,
  `transaction_id` bigint(20) DEFAULT NULL,
  `resource_group_id` varchar(32) DEFAULT NULL,
  `resource_id` varchar(256) DEFAULT NULL,
  `lock_key` varchar(128) DEFAULT NULL,
  `branch_type` varchar(8) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `client_id` varchar(64) DEFAULT NULL,
  `application_data` varchar(2000) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`branch_id`),
  KEY `idx_xid` (`xid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


```

`lock_table`的表结构

```
create table `lock_table` (
  `row_key` varchar(128) not null,
  `xid` varchar(96),
  `transaction_id` long ,
  `branch_id` long,
  `resource_id` varchar(256) ,
  `table_name` varchar(32) ,
  `pk` varchar(32) ,
  `gmt_create` datetime ,
  `gmt_modified` datetime,
  primary key(`row_key`)
);
```

#### 2.2.4 将 Seata 配置添加到 Nacos 中

使用命令如下

```
cd conf
sh nacos-config.sh localhost
```

成功后会提示

```
init nacos config finished, please start seata-server
```

在 Nacos 管理页面应该可以看到有多个 Group 为 SEATA_GROUP 的配置

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019090510533734.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9saWRvbmcxNjY1LmJsb2cuY3Nkbi5uZXQ=,size_16,color_FFFFFF,t_70)

#### 2.2.5 启动 Seata Server

使用db 模式启动

```shell
 cd ..
 sh ./bin/seata-server.sh
```

这时候在 Nacos 的服务列表下面可以看到一个名为serverAddr的服务
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190905110455278.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9saWRvbmcxNjY1LmJsb2cuY3Nkbi5uZXQ=,size_16,color_FFFFFF,t_70)

## 3 使用seata-spring-boot-starter案例分析

`seata-spring-boot-starter`是使用springboot自动装配来简化seata-all的复杂配置。1.5.2可用于替换seata-all，`GlobalTransactionScanner`
自动初始化（依赖SpringUtils）若其他途径实现`GlobalTransactionScanner`初始化，请保证`io.seata.spring.boot.autoconfigure.util.SpringUtils`先初始化；
`seata-spring-boot-starter`默认开启数据源自动代理，用户若再手动配置`DataSourceProxy`将会导致异常。

参考官网中用户购买商品的业务逻辑。整个业务逻辑由4个微服务提供支持：

- 库存服务：扣除给定商品的存储数量。
- 订单服务：根据购买请求创建订单。
- 帐户服务：借记用户帐户的余额。
- 业务服务：处理业务逻辑。

请求逻辑架构
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190905111031350.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9saWRvbmcxNjY1LmJsb2cuY3Nkbi5uZXQ=,size_16,color_FFFFFF,t_70)

## 3.1  github地址

gmall-distributed-transaction：[https://github.com/haibozhang2016/gmall-distributed-transaction]

- gmall-common ：公共模块
- gmall-account ：账户模块
- gmall-order ：订单模块
- gmall-stock ：库存模块
- gmall-business ：业务模块

#### 3.2 账户服务：AccountDubboService

```java
public interface AccountDubboService {

    /**
     * 从账户扣钱
     */
    ObjectResponse decreaseAccount(AccountDTO accountDTO);
}
```

#### 3.3 订单服务：OrderDubboService

```java
public interface OrderDubboService {

    /**
     * 创建订单
     */
    ObjectResponse<OrderDTO> createOrder(OrderDTO orderDTO);
}
```

#### 3.4 库存服务：StockDubboService

```java
public interface StockDubboService {

    /**
     * 扣减库存
     */
    ObjectResponse decreaseStock(CommodityDTO commodityDTO);
}

```

#### 3.5 业务服务：BusinessService

```java
public interface BusinessService {

    /**
     * 出处理业务服务
      * @param businessDTO
     * @return
     */
    ObjectResponse handleBusiness(BusinessDTO businessDTO);
}
```

业务逻辑的具体实现主要体现在 订单服务的实现和业务服务的实现

订单服务的实现

```java
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @DubboReference
    private AccountDubboService accountDubboService;

    /**
     * 创建订单
     * @Param:  OrderDTO  订单对象
     * @Return:  OrderDTO  订单对象
     */
    @Override
    public ObjectResponse<OrderDTO> createOrder(OrderDTO orderDTO) {
        ObjectResponse<OrderDTO> response = new ObjectResponse<>();
        //扣减用户账户
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setUserId(orderDTO.getUserId());
        accountDTO.setAmount(orderDTO.getOrderAmount());
        ObjectResponse objectResponse = accountDubboService.decreaseAccount(accountDTO);

        //生成订单号
        orderDTO.setOrderNo(UUID.randomUUID().toString().replace("-",""));
        //生成订单
        TOrder tOrder = new TOrder();
        BeanUtils.copyProperties(orderDTO,tOrder);
        tOrder.setCount(orderDTO.getOrderCount());
        tOrder.setAmount(orderDTO.getOrderAmount().doubleValue());
        try {
            baseMapper.createOrder(tOrder);
        } catch (Exception e) {
            response.setStatus(RspStatusEnum.FAIL.getCode());
            response.setMessage(RspStatusEnum.FAIL.getMessage());
            return response;
        }

        if (objectResponse.getStatus() != 200) {
            response.setStatus(RspStatusEnum.FAIL.getCode());
            response.setMessage(RspStatusEnum.FAIL.getMessage());
            return response;
        }

        response.setStatus(RspStatusEnum.SUCCESS.getCode());
        response.setMessage(RspStatusEnum.SUCCESS.getMessage());
        return response;
    }
}
```

整个业务的实现逻辑

```java
@Service
@Slf4j
public class BusinessServiceImpl implements BusinessService{

    @DubboReference
    private StockDubboService stockDubboService;

    @DubboReference
    private OrderDubboService orderDubboService;

    private boolean flag;

    /**
     * 处理业务逻辑
     * @Param:
     * @Return:
     */

    @GlobalTransactional(timeoutMills = 300000, name = "dubbo-gts-seata")
    @Override
    public ObjectResponse handleBusiness(BusinessDTO businessDTO) {
        log.info("开始全局事务，XID = " + RootContext.getXID());
        ObjectResponse<Object> objectResponse = new ObjectResponse<>();
        //1、扣减库存
        CommodityDTO commodityDTO = new CommodityDTO();
        commodityDTO.setCommodityCode(businessDTO.getCommodityCode());
        commodityDTO.setCount(businessDTO.getCount());
        ObjectResponse stockResponse = stockDubboService.decreaseStock(commodityDTO);
        //2、创建订单
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(businessDTO.getUserId());
        orderDTO.setCommodityCode(businessDTO.getCommodityCode());
        orderDTO.setOrderCount(businessDTO.getCount());
        orderDTO.setOrderAmount(businessDTO.getAmount());
        ObjectResponse<OrderDTO> response = orderDubboService.createOrder(orderDTO);

        //打开注释测试事务发生异常后，全局回滚功能
//        if (!flag) {
//            throw new RuntimeException("测试抛异常后，分布式事务回滚！");
//        }

        if (stockResponse.getStatus() != 200 || response.getStatus() != 200) {
            throw new DefaultException(RspStatusEnum.FAIL);
        }

        objectResponse.setStatus(RspStatusEnum.SUCCESS.getCode());
        objectResponse.setMessage(RspStatusEnum.SUCCESS.getMessage());
        objectResponse.setData(response.getData());
        return objectResponse;
    }
}
```

## 3.6 使用seata的分布式事务解决方案处理dubbo的分布式事务

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190905113350848.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9saWRvbmcxNjY1LmJsb2cuY3Nkbi5uZXQ=,size_16,color_FFFFFF,t_70)

我们只需要在业务处理的方法`handleBusiness`添加一个注解 `@GlobalTransactional`

```java
    @GlobalTransactional(timeoutMills = 300000, name = "dubbo-gts-seata")
    @Override
    public ObjectResponse handleBusiness(BusinessDTO businessDTO) {}
```

- `timeoutMills`: 超时时间
- `name ` ：事务名称

## 3.7 准备数据库

注意: MySQL必须使用`InnoDB engine`.

创建数据库并导入数据库脚本

```sql
DROP DATABASE IF EXISTS seata;
CREATE DATABASE seata;
USE seata;

DROP TABLE IF EXISTS `t_account`;
CREATE TABLE `t_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) DEFAULT NULL,
  `amount` double(14,2) DEFAULT '0.00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- 
----------------------
-- Records of t_account
-- ----------------------------
INSERT INTO `t_account` VALUES ('1', '1', '4000.00');

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `commodity_code` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT '0',
  `amount` double(14,2) DEFAULT '0.00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_order
-- ----------------------------

-- ----------------------------
-- Table structure for t_stock
-- ----------------------------
DROP TABLE IF EXISTS `t_stock`;
CREATE TABLE `t_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `commodity_code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `commodity_code` (`commodity_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_stock
-- ----------------------------
INSERT INTO `t_stock` VALUES ('1', 'C201901140001', '水杯', '1000');

-- ----------------------------
-- Table structure for undo_log
-- 注意此处0.3.0+ 增加唯一索引 ux_undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of undo_log
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
```

会看到如上的4个表

```shell
+-------------------------+
| Tables_in_seata         |
+-------------------------+
| t_account               |
| t_order                 |
| t_stock               |
| undo_log                |
+-------------------------+
```

这里为了简化我将这个三张表创建到一个库中,使用是三个数据源来实现。

## 3.8 以账号服务`gmall-account`为例 ，分析需要注意的项目配置

### 3.8.1 引入的依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.6.11</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <groupId>com.gmall</groupId>
    <artifactId>distributed-transaction</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>gmall-distributed-transaction</name>
    <description>gmall-distributed-transaction</description>

    <modules>
        <module>gmall-common</module>
        <module>gmall-order</module>
        <module>gmall-account</module>
        <module>gmall-business</module>
        <module>gmall-stock</module>
    </modules>

    <properties>
        <java.version>1.8</java.version>
        <druid.version>1.1.10</druid.version>
        <mybatis.version>1.3.2</mybatis.version>
        <mybatis-plus.version>2.3</mybatis-plus.version>
        <dubbo.version>3.1.1</dubbo.version>
        <cloud.alibaba.version>2021.0.4.0</cloud.alibaba.version>
        <seata.version>1.5.2</seata.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bootstrap</artifactId>
            <version>3.1.0</version>
            <exclusions>
                <exclusion>
                    <artifactId>spring-cloud-commons</artifactId>
                    <groupId>org.springframework.cloud</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>spring-cloud-context</artifactId>
                    <groupId>org.springframework.cloud</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <artifactId>asm</artifactId>
                    <groupId>org.ow2.asm</groupId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>${druid.version}</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>${mybatis.version}</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
            <version>${dubbo.version}</version>
            <exclusions>
                <exclusion>
                    <artifactId>spring</artifactId>
                    <groupId>org.springframework</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>${dubbo.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-nacos</artifactId>
            <version>${dubbo.version}</version>
            <exclusions>
                <exclusion>
                    <artifactId>dubbo-cluster</artifactId>
                    <groupId>org.apache.dubbo</groupId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
            <version>${cloud.alibaba.version}</version>
            <exclusions>
                <exclusion>
                    <artifactId>nacos-client</artifactId>
                    <groupId>com.alibaba.nacos</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>spring-context-support</artifactId>
                    <groupId>com.alibaba.spring</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            <version>${cloud.alibaba.version}</version>
            <exclusions>
                <exclusion>
                    <artifactId>nacos-client</artifactId>
                    <groupId>com.alibaba.nacos</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>spring-context-support</artifactId>
                    <groupId>com.alibaba.spring</groupId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>io.seata</groupId>
            <artifactId>seata-spring-boot-starter</artifactId>
            <version>${seata.version}</version>
            <exclusions>
                <exclusion>
                    <artifactId>druid</artifactId>
                    <groupId>com.alibaba</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>checker-qual</artifactId>
                    <groupId>org.checkerframework</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>error_prone_annotations</artifactId>
                    <groupId>com.google.errorprone</groupId>
                </exclusion>
            </exclusions>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>

```
注意：
- `seata-spring-boot-starter`: 这个是spring-boot seata所需的主要依赖，1.0.0版本开始加入支持。
- `dubbo-spring-boot-starter`: springboot dubbo的依赖

其他的就不一一介绍，其他的一目了然，就知道是干什么的。

### 3.8.2  application.yml配置

```yml
server:
  port: 8102
# datasource config
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://192.168.126.137:3306/seata?useSSL=false&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
    username: root
    password: 123456
  application:
    name: dubbo-account

# dubbo config
dubbo:
  application:
    id: dubbo-account
    name: dubbo-account
    qosEnable: false
  protocol:
    id: dubbo
    name: dubbo
    port: 20883
  registry:
    id: dubbo-account-registry
    address: nacos://192.168.126.137:8848
  config-center:
    address: nacos://192.168.126.137:8848
  metadata-report:
    address: nacos://192.168.126.137:8848

# mybatis-plus config
mybatis-plus:
  mapper-locations: classpath*:/mapper/*.xml
  typeAliasesPackage: gmall.distributed.transaction.*.entity
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto

# Seata Config
seata:
  enabled: true
  application-id: account-seata
  tx-service-group: account-service-seata-service-group # 事务群组（可以每个应用独立取名，也可以使用相同的名字）
  client:
    rm-report-success-enable: true
    rm-table-meta-check-enable: false # 自动刷新缓存中的表结构（默认false）
    rm-report-retry-count: 5 # 一阶段结果上报TC重试次数（默认5）
    rm-async-commit-buffer-limit: 10000 # 异步提交缓存队列长度（默认10000）
    rm:
      lock:
        lock-retry-internal: 10 # 校验或占用全局锁重试间隔（默认10ms）
        lock-retry-times:    30 # 校验或占用全局锁重试次数（默认30）
        lock-retry-policy-branch-rollback-on-conflict: true # 分支事务与其它全局回滚事务冲突时锁策略（优先释放本地锁让回滚成功）
    tm-commit-retry-count:   3 # 一阶段全局提交结果上报TC重试次数（默认1次，建议大于1）
    tm-rollback-retry-count: 3 # 一阶段全局回滚结果上报TC重试次数（默认1次，建议大于1）
    undo:
      undo-data-validation: true # 二阶段回滚镜像校验（默认true开启）
      undo-log-serialization: jackson # undo序列化方式（默认jackson）
      undo-log-table: undo_log  # 自定义undo表名（默认undo_log）
    log:
      exceptionRate: 100 # 日志异常输出概率（默认100）
    support:
      spring:
        datasource-autoproxy: true
  service:
    vgroup-mapping: default # TC 集群（必须与seata-server保持一致）
    enable-degrade: false # 降级开关
    disable-global-transaction: false # 禁用全局事务（默认false）
    grouplist: 127.0.0.1:8091
  transport:
    shutdown:
      wait: 3
    thread-factory:
      boss-thread-prefix: NettyBoss
      worker-thread-prefix: NettyServerNIOWorker
      server-executor-thread-prefix: NettyServerBizHandler
      share-boss-worker: false
      client-selector-thread-prefix: NettyClientSelector
      client-selector-thread-size: 1
      client-worker-thread-prefix: NettyClientWorkerThread
    type: TCP
    server: NIO
    heartbeat: true
    serialization: seata
    compressor: none
    enable-client-batch-send-request: true # 客户端事务消息请求是否批量合并发送（默认true）
  registry:
    file:
      name: file.conf
    type: nacos
    nacos:
      server-addr: localhost:8848
      namespace:
      cluster: default
  config:
    file:
      name: file.conf
    type: nacos
    nacos:
      namespace:
      server-addr: localhost:8848
```

### 3.8.3 AccountExampleApplication 启动类的配置

```java

@SpringBootApplication(scanBasePackages = "gmall.distributed.transaction.account")
@MapperScan({"gmall.distributed.transaction.account.mapper"})
@EnableDubbo(scanBasePackages = "gmall.distributed.transaction.account")
public class AccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }

}


```

- `@EnableDubbo`等同于 `@DubboComponentScan`和 `@EnableDubboConfig`组合

- `@DubboComponentScan` 扫描 classpaths 下类中添加了 `@Service` 和 `@Reference` 将自动注入到spring beans中。
- @EnableDubboConfig 扫描的dubbo的外部化配置。

## 4 启动所有的gmall模块

启动 `gmall-account`、`gmall-order`、`gmall-stock`、`gmall-business`

并且在nocos的控制台查看注册情况: http://192.168.126.137:8848/nacos/#/serviceManagement

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190905131449502.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9saWRvbmcxNjY1LmJsb2cuY3Nkbi5uZXQ=,size_16,color_FFFFFF,t_70)

我们可以看到上面的服务都已经注册成功。

## 5 测试

### 5. 1 发送一个下单请求

使用postman 发送 ：[http://localhost:8104/business/dubbo/buy](http://localhost:8104/business/dubbo/buy)

参数：

```json
{
    "userId":"1",
    "commodityCode":"C201901140001",
    "name":"fan",
    "count":50,
    "amount":"100"
}
```

返回

```json
{
    "status": 200,
    "message": "成功",
    "data": null
}
```

这时候控制台：

```
2022-10-07 15:15:11.776  INFO 12928 --- [eoutChecker_1_1] i.s.core.rpc.netty.NettyPoolableFactory  : register success, cost 33 ms, version:1.5.2,role:TMROLE,channel:[id: 0x982eb788, L:/192.168.126.1:51635 - R:/192.168.126.137:8091]
2022-10-07 15:29:07.357  INFO 12928 --- [nio-8104-exec-2] g.d.t.b.controller.BusinessController    : 请求参数：BusinessDTO(userId=1, commodityCode=C201901140001, name=fan, count=50, amount=100)
2022-10-07 15:29:07.371  INFO 12928 --- [nio-8104-exec-2] io.seata.tm.TransactionManagerHolder     : TransactionManager Singleton io.seata.tm.DefaultTransactionManager@28ea2621
2022-10-07 15:29:07.518  INFO 12928 --- [nio-8104-exec-2] i.seata.tm.api.DefaultGlobalTransaction  : Begin new global transaction [192.168.126.137:8091:9321294899093505]
2022-10-07 15:29:07.523  INFO 12928 --- [nio-8104-exec-2] g.d.t.b.service.BusinessServiceImpl      : 开始全局事务，XID = 192.168.126.137:8091:9321294899093505
2022-10-07 15:29:09.021  INFO 12928 --- [nio-8104-exec-2] i.seata.tm.api.DefaultGlobalTransaction  : Suspending current transaction, xid = 192.168.126.137:8091:9321294899093505
2022-10-07 15:29:09.021  INFO 12928 --- [nio-8104-exec-2] i.seata.tm.api.DefaultGlobalTransaction  : [192.168.126.137:8091:9321294899093505] commit status: Committed```

事务提交成功，

我们来看一下数据库数据变化

t_account
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190905122211274.png)
t_order

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190905122302472.png)
t_stock

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190905122326182.png)
数据没有问题。

### 5.2 测试回滚

我们`gmall-business`将`BusinessServiceImpl`的`handleBusiness2` 下面的代码去掉注释

```
if (!flag) {
  throw new RuntimeException("测试抛异常后，分布式事务回滚！");
}
```

使用postman 发送 ：[http://localhost:8104/business/dubbo/buy2](http://localhost:8104/business/dubbo/buy2)

.响应结果：

```json
{
    "timestamp": "2019-09-05T04:29:34.178+0000",
    "status": 500,
    "error": "Internal Server Error",
    "message": "测试抛异常后，分布式事务回滚！",
    "path": "/business/dubbo/buy"
}
```

#### 5.2.1 business控制台日志

```
2022-10-07 15:45:31.371  INFO 14576 --- [nio-8104-exec-4] g.d.t.b.controller.BusinessController    : 请求参数：BusinessDTO(userId=1, commodityCode=C201901140001, name=fan, count=50, amount=100)
2022-10-07 15:45:31.376  INFO 14576 --- [nio-8104-exec-4] i.seata.tm.api.DefaultGlobalTransaction  : Begin new global transaction [192.168.126.137:8091:9321294899093508]
2022-10-07 15:45:31.376  INFO 14576 --- [nio-8104-exec-4] g.d.t.b.service.BusinessServiceImpl      : 开始全局事务，XID = 192.168.126.137:8091:9321294899093508
2022-10-07 15:45:35.094  INFO 14576 --- [nio-8104-exec-4] i.seata.tm.api.DefaultGlobalTransaction  : Suspending current transaction, xid = 192.168.126.137:8091:9321294899093508
2022-10-07 15:45:35.094  INFO 14576 --- [nio-8104-exec-4] i.seata.tm.api.DefaultGlobalTransaction  : [192.168.126.137:8091:9321294899093508] rollback status: Rollbacked
2022-10-07 15:45:35.095 ERROR 14576 --- [nio-8104-exec-4] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is java.lang.RuntimeException: 测试抛异常后，分布式事务回滚！] with root cause

java.lang.RuntimeException: 测试抛异常后，分布式事务回滚！
	at gmall.distributed.transaction.business.service.BusinessServiceImpl.handleBusiness(BusinessServiceImpl.java:55) ~[classes/:na]
	at gmall.distributed.transaction.business.service.BusinessServiceImpl$$FastClassBySpringCGLIB$$2efa4f9c.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) ~[spring-core-5.3.22.jar:5.3.22]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:793) ~[spring-aop-5.3.22.jar:5.3.22]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) ~[spring-aop-5.3.22.jar:5.3.22]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:763) ~[spring-aop-5.3.22.jar:5.3.22]
	at io.seata.spring.annotation.GlobalTransactionalInterceptor$2.execute(GlobalTransactionalInterceptor.java:184) ~[seata-all-1.5.2.jar:1.5.2]
	at io.seata.tm.api.TransactionalTemplate.execute(TransactionalTemplate.java:127) ~[seata-all-1.5.2.jar:1.5.2]
	at io.seata.spring.annotation.GlobalTransactionalInterceptor.handleGlobalTransaction(GlobalTransactionalInterceptor.java:181) ~[seata-all-1.5.2.jar:1.5.2]
	at io.seata.spring.annotation.GlobalTransactionalInterceptor.invoke(GlobalTransactionalInterceptor.java:150) ~[seata-all-1.5.2.jar:1.5.2]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) ~[spring-aop-5.3.22.jar:5.3.22]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:763) ~[spring-aop-5.3.22.jar:5.3.22]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:708) ~[spring-aop-5.3.22.jar:5.3.22]
	at gmall.distributed.transaction.business.service.BusinessServiceImpl$$EnhancerBySpringCGLIB$$8611641e.handleBusiness(<generated>) ~[classes/:na]
	at gmall.distributed.transaction.business.controller.BusinessController.handleBusiness(BusinessController.java:30) ~[classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_77]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_77]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_77]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_77]
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:205) ~[spring-web-5.3.22.jar:5.3.22]
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:150) ~[spring-web-5.3.22.jar:5.3.22]
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:117) ~[spring-webmvc-5.3.22.jar:5.3.22]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:895) ~[spring-webmvc-5.3.22.jar:5.3.22]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:808) ~[spring-webmvc-5.3.22.jar:5.3.22]
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87) ~[spring-webmvc-5.3.22.jar:5.3.22]
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1070) ~[spring-webmvc-5.3.22.jar:5.3.22]
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:963) ~[spring-webmvc-5.3.22.jar:5.3.22]
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006) ~[spring-webmvc-5.3.22.jar:5.3.22]
	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:909) ~[spring-webmvc-5.3.22.jar:5.3.22]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:681) ~[tomcat-embed-core-9.0.65.jar:4.0.FR]
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883) ~[spring-webmvc-5.3.22.jar:5.3.22]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:764) ~[tomcat-embed-core-9.0.65.jar:4.0.FR]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:227) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53) ~[tomcat-embed-websocket-9.0.65.jar:9.0.65]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-5.3.22.jar:5.3.22]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:117) ~[spring-web-5.3.22.jar:5.3.22]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-5.3.22.jar:5.3.22]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:117) ~[spring-web-5.3.22.jar:5.3.22]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter.doFilterInternal(WebMvcMetricsFilter.java:96) ~[spring-boot-actuator-2.6.11.jar:2.6.11]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:117) ~[spring-web-5.3.22.jar:5.3.22]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-5.3.22.jar:5.3.22]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:117) ~[spring-web-5.3.22.jar:5.3.22]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:197) ~[tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:97) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:541) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:135) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:360) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:399) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:890) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1789) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) [tomcat-embed-core-9.0.65.jar:9.0.65]
	at java.lang.Thread.run(Thread.java:745) [na:1.8.0_77]
```

#### 5.2.2 account服务控制台日志

```bash
2020-01-08 11:05:35.523  INFO 23416 --- [:20883-thread-8] i.s.s.i.a.dubbo.AccountDubboServiceImpl  : 全局事务id ：192.168.10.103:8091:2032180177
2020-01-08 11:05:35.793  INFO 23416 --- [:20883-thread-8] i.s.common.loader.EnhancedServiceLoader  : load LoadBalance[null] extension by class[io.seata.discovery.loadbalance.RandomLoadBalance]
2020-01-08 11:05:35.908  WARN 23416 --- [:20883-thread-8] i.s.common.loader.EnhancedServiceLoader  : load [io.seata.rm.datasource.undo.parser.ProtostuffUndoLogParser] class fail. io/protostuff/runtime/RuntimeEnv
2020-01-08 11:05:35.909  INFO 23416 --- [:20883-thread-8] i.s.common.loader.EnhancedServiceLoader  : load UndoLogParser[jackson] extension by class[io.seata.rm.datasource.undo.parser.JacksonUndoLogParser]
2020-01-08 11:05:37.281  INFO 23416 --- [atch_RMROLE_1_8] i.s.core.rpc.netty.RmMessageListener     : onMessage:xid=192.168.10.103:8091:2032180177,branchId=2032180189,branchType=AT,resourceId=jdbc:mysql://127.0.0.1:3306/seata,applicationData=null
2020-01-08 11:05:37.283  INFO 23416 --- [atch_RMROLE_1_8] io.seata.rm.AbstractRMHandler            : Branch Rollbacking: 192.168.10.103:8091:2032180177 2032180189 jdbc:mysql://127.0.0.1:3306/seata
2020-01-08 11:05:37.477  INFO 23416 --- [atch_RMROLE_1_8] i.s.r.d.undo.AbstractUndoLogManager      : xid 192.168.10.103:8091:2032180177 branch 2032180189, undo_log deleted with GlobalFinished
2020-01-08 11:05:37.478  INFO 23416 --- [atch_RMROLE_1_8] io.seata.rm.AbstractRMHandler            : Branch Rollbacked result: PhaseTwo_Rollbacked
```

#### 5.2.3 order服务控制台日志

```bash
2020-01-08 11:05:35.492  INFO 17296 --- [:20880-thread-2] i.s.s.i.o.dubbo.OrderDubboServiceImpl    : 全局事务id ：192.168.10.103:8091:2032180177
2020-01-08 11:05:36.470  INFO 17296 --- [:20880-thread-2] i.s.common.loader.EnhancedServiceLoader  : load LoadBalance[null] extension by class[io.seata.discovery.loadbalance.RandomLoadBalance]
2020-01-08 11:05:36.648  WARN 17296 --- [:20880-thread-2] i.s.common.loader.EnhancedServiceLoader  : load [io.seata.rm.datasource.undo.parser.ProtostuffUndoLogParser] class fail. io/protostuff/runtime/RuntimeEnv
2020-01-08 11:05:36.650  INFO 17296 --- [:20880-thread-2] i.s.common.loader.EnhancedServiceLoader  : load UndoLogParser[jackson] extension by class[io.seata.rm.datasource.undo.parser.JacksonUndoLogParser]
2020-01-08 11:05:36.895  INFO 17296 --- [atch_RMROLE_1_8] i.s.core.rpc.netty.RmMessageListener     : onMessage:xid=192.168.10.103:8091:2032180177,branchId=2032180192,branchType=AT,resourceId=jdbc:mysql://127.0.0.1:3306/seata,applicationData=null
2020-01-08 11:05:36.897  INFO 17296 --- [atch_RMROLE_1_8] io.seata.rm.AbstractRMHandler            : Branch Rollbacking: 192.168.10.103:8091:2032180177 2032180192 jdbc:mysql://127.0.0.1:3306/seata
2020-01-08 11:05:37.152  INFO 17296 --- [atch_RMROLE_1_8] i.s.r.d.undo.AbstractUndoLogManager      : xid 192.168.10.103:8091:2032180177 branch 2032180192, undo_log deleted with GlobalFinished
2020-01-08 11:05:37.153  INFO 17296 --- [atch_RMROLE_1_8] io.seata.rm.AbstractRMHandler            : Branch Rollbacked result: PhaseTwo_Rollbacked
```

#### 5.2.4 order服务控制台日志

```bash
2020-01-08 11:05:31.478  INFO 24100 --- [:20888-thread-2] i.s.common.loader.EnhancedServiceLoader  : load ContextCore[null] extension by class[io.seata.core.context.ThreadLocalContextCore]
2020-01-08 11:05:31.478  INFO 24100 --- [:20888-thread-2] i.s.s.i.s.dubbo.StockDubboServiceImpl  : 全局事务id ：192.168.10.103:8091:2032180177
2020-01-08 11:05:32.097  INFO 24100 --- [:20888-thread-2] i.s.common.loader.EnhancedServiceLoader  : load LoadBalance[null] extension by class[io.seata.discovery.loadbalance.RandomLoadBalance]
2020-01-08 11:05:33.130  WARN 24100 --- [:20888-thread-2] i.s.common.loader.EnhancedServiceLoader  : load [io.seata.rm.datasource.undo.parser.ProtostuffUndoLogParser] class fail. io/protostuff/runtime/RuntimeEnv
2020-01-08 11:05:33.131  INFO 24100 --- [:20888-thread-2] i.s.common.loader.EnhancedServiceLoader  : load UndoLogParser[jackson] extension by class[io.seata.rm.datasource.undo.parser.JacksonUndoLogParser]
2020-01-08 11:05:37.549  INFO 24100 --- [atch_RMROLE_1_8] i.s.core.rpc.netty.RmMessageListener     : onMessage:xid=192.168.10.103:8091:2032180177,branchId=2032180182,branchType=AT,resourceId=jdbc:mysql://127.0.0.1:3306/seata,applicationData=null
2020-01-08 11:05:37.551  INFO 24100 --- [atch_RMROLE_1_8] io.seata.rm.AbstractRMHandler            : Branch Rollbacking: 192.168.10.103:8091:2032180177 2032180182 jdbc:mysql://127.0.0.1:3306/seata
2020-01-08 11:05:37.692  INFO 24100 --- [atch_RMROLE_1_8] i.s.r.d.undo.AbstractUndoLogManager      : xid 192.168.10.103:8091:2032180177 branch 2032180182, undo_log deleted with GlobalFinished
2020-01-08 11:05:37.693  INFO 24100 --- [atch_RMROLE_1_8] io.seata.rm.AbstractRMHandler            : Branch Rollbacked result: PhaseTwo_Rollbacked
```

我们查看数据库数据，已经回滚，和上面的数据一致。

到这里一个简单的案例基本就分析结束。感谢你的学习。

## 三、项目开发过程问题
### 1. 因为使用的Nacos2.x导致连接Nacos有问题

#### 1.1 问题原因

官网回答：https://nacos.io/zh-cn/docs/2.0.0-compatibility.html
Nacos2.0版本相比1.X新增了gRPC的通信方式，因此需要增加2个端口。新增端口是在配置的主端口(server.port)基础上，进行一定偏移量自动生成。
使用VIP/nginx请求时，需要配置成TCP转发，不能配置http2转发，否则连接会被nginx断开，因此nginx需要新增两个TCP端口：9848和9849

#### 1.2 解决方法
a) 在顶级配置文件(/etc/nginx/nginx.conf)中添加如下配置块:
```
stream {
    upstream nacos-9848 {
        server 192.168.126.137:9848;
        server 192.168.126.137:9858;
        server 192.168.126.137:9868;
    }
    upstream nacos-9849 {
        server 192.168.126.137:9849;
        server 192.168.126.137:9859;
        server 192.168.126.137:9869;
    }
    server {
        listen 9848;
        proxy_pass nacos-9848;
    }
    server {
        listen 9849;
        proxy_pass nacos-9849;
    }
}
```
实现监听和转发9848、9849端口.
```
# 检查配置文件
nginx -t  
# 重新加载配置
nginx -s reload  
```
<p>
    注：TCP请求配置参考<a href="https://www.cnblogs.com/airoot/p/14958783.html">Nginx配置TCP请求转发</a>
</p>

b) Nacos高可用部署新增开发端口
```
-p 8848:8848 -p 9848:9848 
```
### 2. Seata高可用Docker集群部署，连接有问题

问题：只有8091节点可以正常连接，其它连接异常
```
添加 SERVER_NODE 用于指定seata-server节点ID
-e SERVER_NODE=XX \
```

## 四、spring-cloud-alibaba
<a href="毕业版本依赖关系(推荐使用)">毕业版本依赖关系(推荐使用)</a>
