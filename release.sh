#!/usr/bin/env bash
function release() {

  mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$RELEASE
  git add .
  git commit --message "Release for v$RELEASE"

  git tag -s v$RELEASE -m "v$RELEASE"

  git push && git push --tags

  mvn clean deploy
}

echo "Release Version: "
read RELEASE

echo "(release version is $RELEASE) Are You Sure? [Y/n]"
read input

case $input in
	[yY])
	  release
		;;
	[nN])
		echo "exit"
		exit 1
		;;
	*)
		echo "Invalid input & exit"
		exit 1
esac


