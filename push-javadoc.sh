#!/usr/bin/env bash

echo "==> Publishing site..."

BASE_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

if [ -e "${BASE_DIR}/target/site/index.html" ]
then
    echo "site present"
else
    echo "Please run 'mvn clean site' first"
    exit;
fi

TEMP_DIR="/tmp/site${RANDOM}"
echo "==> Working dir is ${TEMP_DIR}"

mkdir -p ${TEMP_DIR}
cd ${TEMP_DIR}
git clone --quiet --branch=gh-pages https://github.com/egateam/jintspan.git gh-pages > /dev/null

cd gh-pages
git rm -rf *
cp -Rf ${BASE_DIR}/target/site/* .
git add -f .
git commit -m "Latest docs auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null

rm -fr ${TEMP_DIR}

echo "==> Published Javadoc to gh-pages."
