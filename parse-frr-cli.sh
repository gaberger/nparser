#!/usr/bin/env bash
regex='DEFUN\s+\(\w+\,\s+\w+\,\s+\"([a-zA-Z\s<|>.:-]*)'

egrep -h --include="*.c" -R -a5 "^DEFUN" $1
