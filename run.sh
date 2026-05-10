#!/bin/bash
# Dunkwa FPU Weekly Duty Roster - Launch Script

# Try to find Java in standard locations
if command -v java &> /dev/null; then
    JAVA_CMD="java"
elif [ -n "$JAVA_HOME" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
else
    echo "Error: Java is not installed or not found in PATH"
    echo "Please install Java 17 or later"
    exit 1
fi

# Check Java version
JAVA_VERSION=$($JAVA_CMD -version 2>&1 | grep -oP '(?<=version ")[^"]*' | cut -d_ -f1)
if [[ ! "$JAVA_VERSION" =~ ^[0-9]+\.[0-9]+ ]]; then
    # Try alternative parsing
    JAVA_VERSION=$($JAVA_CMD -version 2>&1 | awk -F'"' '{print $2}' | cut -d_ -f1)
fi

echo "Found Java: $JAVA_VERSION"

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Run the application
JAR_FILE="$SCRIPT_DIR/build/weekly-duty-roster.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: $JAR_FILE not found"
    echo "Please run: gradle build (or use the build script)"
    exit 1
fi

echo "Starting Dunkwa FPU Weekly Duty Roster..."
$JAVA_CMD -jar "$JAR_FILE"
