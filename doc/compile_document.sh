#!/usr/bin/env bash
amwiki -e github-wiki /home/yuanyifan/GitHub/GeoScala.wiki
cd /home/yuanyifan/GitHub/GeoScala.wiki
git add *
git commit -m "update documents"
git push