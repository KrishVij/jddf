<p align="center">
  <img src="assets/jddf.png.png" width="200" alt="JDDF Logo"/>
</p>

# JDDF — Java Document Dynamic Framework

**JDDF** A modular Java CLI for low-level PDF processing, enabling programmatic control over text, images, and structure, it is a Java-powered Swiss Army knife for PDF editing.

---

## Features
-  **Text Color Manipulation** — Instantly recolor PDF text using RGB values, extract all the text and search for a term in   
-  **Image to PDF Conversion** — Merge multiple images or directories into a single PDF, single image to pdf as well  
-  **Low-Level PDF Access** — Directly interact with PDFBox for fine-grained document editing  
-  **Portable Executable** — Single shaded JAR (`jddf.jar`) that runs anywhere with Java  
-  **CLI Simplicity** — Modular subcommands with automatic help menus and argument validation which are intuitive 

---

## Example Usage

```bash
# Display available commands
jddf --help

# Change text color in a PDF
jddf text-color "input.pdf" 255 0 0 "output.pdf"

# Merge multiple images into a PDF
jddf merge-images "output.pdf" "img1.png" "img2.png" 

# Convert an entire directory of images into a single PDF
jddf dir-to-pdf "images/" "output.pdf"
```
## Installation

JDDF runs anywhere **Java 21+** is recommended.
#### **Download the latest release**
Head to the [Releases](https://github.com/<your-username>/jddf/releases) section and download the latest jddf-release:  
1. Make sure you have java installed on your system if not then install it [from here](https://www.oracle.com/in/java/technologies/downloads/)
### Linux / macOS   
---
**For Linux/ mac os users u can simply follow the steps given below**
```bash
#!/usr/bin/env bash
## JDDF launcher (Unix). Safe: does not modify system files.
DIR="$(cd "$(dirname "$0")" && pwd)"
java -jar "$DIR/target/jddf-1.0-SNAPSHOT.jar" "$@"
## 2. Create a file called jddf.sh and copy the above code there
## 3. Run `chmod +x jddf` to make the script executable.
## 4. Add the jddf executable to your `$PATH` or `usr/bin/`
## 5. Now you are ready to go!!
```
## For Windows

1. Make sure you have Java installed on your system if not then install it [from here](https://www.oracle.com/in/java/technologies/downloads/)
2. Extract the java-release zip folder and then copy this folder's path(make sure it has the .bat file) into your own system's PATH
3. Then you are ready to go!

## Issues/Planned features

1. No Tab compeletion for both Windows and Unix-like OS.
2. No Scaling for images as of yet.
3. The commands are too high-level in the future the focus will be to give users more low-level functionality.
4. Some way for users to access raw Bytes of the document so that they can perform task according to their needs.
5. Add render-view command in the `display` subcommands to give users live access to pdf pages in the terminal
