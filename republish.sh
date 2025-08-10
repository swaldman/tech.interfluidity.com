#!/bin/bash

./tech-site-gen       &&
git add .             &&
git commit -m "$1"    &&
git push              &&
./tickle-pull-tech.sh

