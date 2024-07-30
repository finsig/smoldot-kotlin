#! /bin/zsh -e

zsh build_smoldotlibs.sh

docker build -t "finsig:Dockerfile" .

docker run --rm -v \
  `pwd`:/project \
  finsig:Dockerfile bash -c 'cd /project; \
  ./gradlew smoldotkotlin:dokkaHtml'  \
  --platform=linux/amd64