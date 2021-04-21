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
#mvn install -pl :org.openhab.binding.shelly karaf:kar clean install
cp target/org.openhab.binding.shelly-2.5.*-SNAPSHOT.jar ~/Dev/myfiles/shelly/
#cp target/org.openhab.binding.shelly-2.5.*-SNAPSHOT.kar ~/Dev/myfiles/shelly/

echo Copy code to V3, update Shelly from v2 to v3
./copy3.sh
echo Build Version 3
cd ~/Dev/openhab-3/git/openhab-addons/bundles/org.openhab.binding.shelly
#mvn clean install
mvn clean install -Dohc.version=3.0.1
if [ $? -ne 0 ]
then
	echo Build version 3 failed
	exit
fi
#mvn -Dohc.version=3.0.1 clean install -Dohc.version=3.0.1 -pl :org.openhab.binding.shelly karaf:kar
cp ~/Dev/openhab-3/git/openhab-addons/bundles/org.openhab.binding.shelly/target/org.openhab.binding.shelly-3.*-SNAPSHOT.jar ~/Dev/myfiles/shelly/
#cp ~/Dev/openhab-3/git/openhab-addons/bundles/org.openhab.binding.shelly/target/org.openhab.binding.shelly-3.*-SNAPSHOT.kar ~/Dev/myfiles/shelly/

echo Pushing updates
cd ~/Dev/myfiles
./push.sh

