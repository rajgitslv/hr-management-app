#!/bin/bash

# This script adds getters to all event classes that need them

FILES=$(find src/main/java -name "*Event.java" ! -name "DomainEvent.java" ! -name "EmployeeCreatedEvent.java")

for file in $FILES; do
    # Check if file already has getEventId method
    if ! grep -q "public UUID getEventId()" "$file"; then
        # Find the line with getEventType method
        line_num=$(grep -n "public String getEventType()" "$file" | cut -d: -f1)

        if [ ! -z "$line_num" ]; then
            # Insert getters before getEventType
            sed -i "" "${line_num}i\\
\\
    @Override\\
    public UUID getEventId() {\\
        return eventId;\\
    }\\
\\
    @Override\\
    public Instant getOccurredOn() {\\
        return occurredOn;\\
    }\\
" "$file"
        fi
    fi
done

echo "Getters added to all event files"
