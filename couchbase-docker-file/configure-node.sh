#!/bin/bash

set -x
set -m

/entrypoint.sh couchbase-server &

sleep 15

# Setup services
curl -v http://127.0.0.1:8091/node/controller/setupServices -d services=kv%2Cn1ql%2Cindex

# Setup credentials
curl -v http://127.0.0.1:8091/settings/web -d port=8091 -d username=Administrator -d password=password

# Setup Memory Optimized Indexes
curl -i -u Administrator:password -X POST http://127.0.0.1:8091/settings/indexes -d 'storageMode=memory_optimized'

# Load bucket
curl -v -u Administrator:password -X POST http://127.0.0.1:8091/pools/default/buckets -d 'name=Scheduler&ramQuotaMB=300&bucketType=couchbase&authType=none'

# Create bucket authorization
curl -v -u Administrator:password -X PUT http://127.0.0.1:8091/settings/rbac/users/local/Scheduler -d 'name=Scheduler&password=password&roles=bucket_full_access[Scheduler]'

echo "Type: $TYPE"

if [ "$TYPE" = "WORKER" ]; then
  echo "Sleeping ..."
  sleep 15

  #IP=`hostname -s`
  IP=`hostname -I | cut -d ' ' -f1`
  echo "IP: " $IP

  echo "Auto Rebalance: $AUTO_REBALANCE"
  if [ "$AUTO_REBALANCE" = "true" ]; then
    couchbase-cli rebalance --cluster=$COUCHBASE_MASTER:8091 --user=Administrator --password=password --server-add=$IP --server-add-username=Administrator --server-add-password=password
  else
    couchbase-cli server-add --cluster=$COUCHBASE_MASTER:8091 --user=Administrator --password=password --server-add=$IP --server-add-username=Administrator --server-add-password=password
  fi;
fi;

fg 1