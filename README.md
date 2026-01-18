# Reader–Writer Synchronization Problem

## Overview

The **Reader–Writer Problem** is a classic **synchronization problem** in computer science that focuses on managing concurrent access to a shared resource.  
This project demonstrates how multiple threads (readers and writers) can safely interact with shared data without causing race conditions or data inconsistency.

The implementation ensures **thread safety** while maintaining **maximum concurrency**.


## Problem Statement

In a multi-threaded environment, several threads may attempt to access the same shared resource simultaneously.

- **Readers** only read the data
- **Writers** both read and modify the data

The challenge is to:
- Prevent writers from modifying data while readers are reading
- Prevent readers from reading while a writer is writing
- Allow multiple readers to read at the same time
- Allow only one writer to access the resource at a time


## Types of Threads

### Readers
- Read-only access to the shared resource
- Multiple readers can access the resource simultaneously
- Must wait if a writer is writing

### Writers
- Read and write access to the shared resource
- Only one writer can access the resource at a time
- Must wait if any reader or writer is active


## 🔒 Synchronization Rules

The following rules are strictly enforced:

- Multiple readers can read simultaneously  
- Only one writer can write at a time  
- No reader can read while a writer is writing  
- No writer can write while readers are reading  

These rules guarantee **data consistency** and **safe concurrent execution**.


## How the Solution Works (High-Level)

1. Synchronization mechanisms such as **mutexes, semaphores, or locks** are used.
2. A counter tracks the number of active readers.
3. Writers are blocked until:
   - No readers are active
   - No other writer is writing
4. Readers are blocked only when a writer is active.

This approach balances **performance** and **correctness** by allowing parallel reads while protecting writes.


## Why This Matters

The Reader–Writer problem is widely used in real-world systems such as:

- Databases
- Operating systems
- File systems
- Web servers
- Caching layers

Understanding this problem demonstrates strong knowledge of:
- Concurrency
- Critical sections
- Thread synchronization
- Operating system fundamentals


## Skills Demonstrated

- Multithreading and concurrency control
- Synchronization primitives (mutex/semaphore/lock)
- Race condition prevention
- Thread-safe programming
- System-level problem solving


## Example Scenario

- 5 reader threads access a shared resource → allowed
- 1 writer thread requests access → waits
- All readers finish → writer enters and updates the resource
- Writer exits → readers can resume
