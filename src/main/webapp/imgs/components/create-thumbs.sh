#!/usr/bin/env bash

export simmer_maven_home=/Users/joe/Development/idea-workspace/simmer-maven/src

function makethumb {
   echo "Saving to: `lowercase $1`.png"
   convert -size 100x100 -gravity center label:$1 `lowercase $1`.png
}

function lowercase {
    echo "$1" | awk '{print tolower($0)}'
}

function list-thumb-names {

    for i in `$simmer_maven_home/list-components.js`;
    do
        echo `basename $i`;
    done;
}

for j in `list-thumb-names`;
do
#    echo creating thumb `lowercase $j`.png for $j
    makethumb $j;
done;