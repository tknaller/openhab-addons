cd ~/Dev/openhab-3/git/openhab-addons/bundles/org.openhab.binding.shelly
rm -rf src/main/java/
mkdir src/main/java
cp -R ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.shelly/src/main/java/ src/main/java/
rm -rf src/main/resources/OH-INF/
mkdir src/main/resources/OH-INF
cp -R ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.shelly/src/main/resources/ESH-INF/ src/main/resources/OH-INF/
cp -R ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.shelly/src/main/resources/sniplets/ src/main/resources/sniplets/
cp -R ~/Dev/openhab-2-5-x/git/openhab-addons/bundles/org.openhab.binding.shelly/src/main/resources/images/ src/main/resources/images/
~/Dev/myfiles/convert_v2_v3.sh

