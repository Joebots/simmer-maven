#!/usr/bin/env node

var fs = require('fs');
var home = "/Users/joe/Development/idea-workspace/simmer-maven/src";

function list(){

    var cmd = `find ${home} -name *Elm.java | sed "s/.*elcomp\\/\\(.*\\)Elm.java/\\1/g" | sort -u`;
    var exec = require('child_process').execSync(cmd) + "";
    var list = (exec.toString()).split("\n");

    for (var i=0; i<list.length-1; i++) {
        console.log(list[i]);
    }
}

list();
