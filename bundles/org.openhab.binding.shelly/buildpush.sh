echo Updating documentation
cp ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.shelly/README.md ~/Dev/openhab-3/git/openhab-addons/bundles/org.openhab.binding.shelly/
cp -R ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.shelly/doc/ ~/Dev/openhab-3/git/openhab-addons/bundles/org.openhab.binding.shelly/doc/
cp ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.shelly/README.md ~/Dev/myfiles/shelly/
cp -R ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.shelly/doc/ ~/Dev/myfiles/shelly/doc/

echo Update formatting
mvn spotless:apply
if [ $? -ne 0 ]
then
	exit
fi

echo Build Version 2
mvn clean install
if [ $? -ne 0 ]
then
	echo Build version 2 failed
	exit
fi
cp target/org.openhab.binding.shelly-2.5.*-SNAPSHOT.jar ~/Dev/myfiles/shelly/

echo Copy code to V3, update Shelly from v2 to v3
./copy3.sh
echo Build Version 3
mvn clean install
if [ $? -ne 0 ]
then
	echo Build version 3 failed
	exit
fi
cp target/org.openhab.binding.shelly-3.*-SNAPSHOT.jar ~/Dev/myfiles/shelly/

echo Pushing updates
cd ~/Dev/myfiles
./push.sh

cd ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.shelly
