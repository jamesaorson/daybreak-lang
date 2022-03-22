#! /bin/bash

set -ex

if [ -z ${DAYBREAK_BOOTSTRAP} ];
then
	echo "Must define env var: DAYBREAK_BOOTSTRAP"
	exit 1
fi

if [ -z ${DAYBREAK_TEST} ];
then
	echo "Must define env var: DAYBREAK_TEST"
	exit 1
fi

if [ -z ${JENKINS_USER} ];
then
	echo "Must define env var: JENKINS_USER"
	exit 1
fi

if [ -z ${JENKINS_GROUP} ];
then
	echo "Must define env var: JENKINS_GROUP"
	exit 1
fi