# Readers-Writers Problem - Java Implementation

## Overview
The Readers-Writers problem is a classical synchronization problem that deals with managing concurrent access to a shared resource by multiple reader and writer threads.

## Problem Statement
- **Readers**: Only read the shared data and can access it simultaneously
- **Writers**: Modify the shared data and require exclusive access
- **Constraints**:
  - Multiple readers can read simultaneously
  - Only one writer can write at a time
  - No reader can read while a writer is writing

## Implementations Included

### 1. **Reader-Preference Solution** (`ReaderPreferenceSolution.java`)
- Prioritizes readers over writers
- Multiple readers can access the resource simultaneously
- **Drawback**: Writers may starve if readers keep arriving

### 2. **Writer-Preference Solution** (`WriterPreferenceSolution.java`)
- Prioritizes writers over readers
- Writers get preference when both readers and writers are waiting
- **Drawback**: Readers may starve if writers keep arriving

### 3. **Fair/Starve-Free Solution** (`FairSolution.java`)
- Uses a FIFO queue to ensure fairness
- No thread starvation occurs
- Threads are served in the order they arrive

## Project Structure

```
Reader-Writer-Problem/
├── src/              # Java source files (.java)
├── bin/              # Compiled class files (.class)
└── README.md         # Project documentation
```

## How to Run

### Compile all files:
```bash
javac -d bin src/*.java
```

### Run each solution:
```bash
# Reader-Preference Solution
java -cp bin ReaderPreferenceSolution

# Writer-Preference Solution
java -cp bin WriterPreferenceSolution

# Fair Solution
java -cp bin FairSolution

# Java Built-In Solution
java -cp bin JavaBuiltInSolution
```

## Key Concepts Demonstrated
- **Semaphores/Locks**: Mutual exclusion and synchronization
- **ReadWriteLock**: Java's built-in support for reader-writer synchronization
- **Thread Safety**: Proper synchronization to avoid race conditions
- **Starvation**: Understanding and preventing thread starvation
