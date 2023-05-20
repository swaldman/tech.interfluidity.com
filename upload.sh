#!/bin/bash

./tech-site-gen.sh
pushd public
rsync -av . swaldman@tickle.mchange.com:/home/web/public/tech
popd

