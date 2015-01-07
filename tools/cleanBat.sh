#!/bin/bash
for file in data/*/pids; do
    for pid in $(ls $file); do
        echo "kill" $pid
        kill $pid
    done
done
for worker in data/workers/*/pids; do
    for workerPid in $(ls $worker); do
        echo "kill worker" $workerPid
        kill $workerPid
    done
done

rm -rf data

rm -rf data
rm logs/*
read -p "任意键继续..."
