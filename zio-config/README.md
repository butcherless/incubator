# Learning zio-config

- environment variables
- hocon file

## Config

    export DL_FILENAME=/tmp/dep-list.log
    export DL_EXCLUSIONS=com.cmartin.learn

# Run

    sbt "cls; zioConfig/runMain com.cmartin.learn.SimpleApp"
