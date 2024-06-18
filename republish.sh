#!/bin/bash

./tech-site-gen
git add .
git commit -m "$1"
git push
pushd public
rsync -av . swaldman@tickle.mchange.com:/home/web/public/tech
popd

