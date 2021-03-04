cd ~/Dev/openhab-3/git/openhab-addons/bundles/org.openhab.binding.carnet
rm -rf src/main/java/
mkdir src/main/java
cp -R ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.carnet/src/main/java/ src/main/java/
rm -rf src/main/resources/OH-INF/
mkdir src/main/resources/OH-INF
cp -R ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.carnet/src/main/resources/ESH-INF/ src/main/resources/OH-INF/
~/Dev/myfiles/convert_v2_v3.sh

echo Updating documentation
cp ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.carnet/README.md ~/Dev/openhab-3/git/openhab-addons/bundles/org.openhab.binding.carnet/
cp ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.carnet/README.md ~/Dev/myfiles/carnet/
