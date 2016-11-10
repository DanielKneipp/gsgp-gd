#!/usr/bin/env bash

JAR_FILE='/home/daniel/projects/GSGP-GD/gsgp-gd.jar'
INSTANCE_PATHS=(
    '/home/daniel/.gsgp-gd/experiments/scripts/bestGDDimSel/' 
) 

for p in ${INSTANCE_PATHS[*]}; do
    configs=$(find $p)
    for c in $configs; do
        if [[ -f $c ]]; then
            echo java -jar $JAR_FILE -p $c
            java -Xmx13G -XX:PermSize=256m -XX:MaxPermSize=512m -jar $JAR_FILE -p $c
        fi
    done
done
