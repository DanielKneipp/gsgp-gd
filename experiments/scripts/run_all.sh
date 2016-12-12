#!/usr/bin/env bash

JAR_FILE='/home/daniel/projects/GSGP-GD/gsgp-gd.jar'
INSTANCE_PATHS=(
    '/home/daniel/.gsgp-gd/experiments/scripts/gsgpDimSel10/'
    '/home/daniel/.gsgp-gd/experiments/scripts/gsgpDimSel05/'
    '/home/daniel/.gsgp-gd/experiments/scripts/gsgpDimSel025/'
    '/home/daniel/.gsgp-gd/experiments/scripts/gsgpDimSel01/'
) 

for p in ${INSTANCE_PATHS[*]}; do
    configs=$(find $p)
    for c in $configs; do
        if [[ -f $c ]]; then
            echo java -jar $JAR_FILE -p $c
            java -Xmx16G -jar $JAR_FILE -p $c
        fi
    done
done
