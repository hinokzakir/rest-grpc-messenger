rm -r -f /tmp/docs
mkdir /tmp/docs
mkdir /tmp/docs/src

cp -r src /tmp/docs

cd /tmp/docs/src
find . -type f -name "*.java" | xargs javadoc -d /tmp/docs/ads/docs

cd /tmp/docs
zip -r umu-ads-a1-1.2.6-docs.zip ads
