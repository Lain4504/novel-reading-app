#!/bin/bash

echo "Starting Novel Reading App API..."

# Check if MongoDB is running
if ! pgrep -x "mongod" > /dev/null; then
    echo "MongoDB is not running. Please start MongoDB first."
    echo "On Windows: net start MongoDB"
    echo "On macOS: brew services start mongodb-community"
    echo "On Linux: sudo systemctl start mongod"
    exit 1
fi

# Set active profile
export SPRING_PROFILES_ACTIVE=dev

# Run the application
./gradlew bootRun
