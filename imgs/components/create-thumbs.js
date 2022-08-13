#!/usr/bin/env bash

export simmer_maven_home=/Users/joe/Development/idea-workspace/simmer-maven/src

function makethumb {
    export f=`echo $1 | sed 's/.*\\\//g'`;
    echo $f
    #echo "Saving to: $1.png"
    #mkdir -p $1.png
    #convert -size 100x100 -gravity center label:$1 `lowercase $1`.png
}

function lowercase {
    echo "$1" | awk '{print tolower($0)}'
}

function list-components {

    for i in `$simmer_maven_home/list-components.js`;
    do
        echo $i;
    done;
}

for j in `list-components`;
do
#    echo creating thumb `lowercase $j`.png for $j
    makethumb $j;
done;