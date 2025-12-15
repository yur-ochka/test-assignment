# ====================================================================
# Configuration
# ====================================================================

SRC_MAIN := src/main/java
SRC_TEST := src/test/java
RES_TEST := src/test/resources

OUT_DIR := out
OUT_MAIN := $(OUT_DIR)/main
OUT_TEST := $(OUT_DIR)/test

LIB_DIR := lib

# --- JUnit Dependencies ---
# JUNIT 5 Platform Console (used for RUNNING tests)
JUNIT_PLATFORM := $(LIB_DIR)/junit-platform-console-standalone-6.0.1.jar

# JUNIT 4 Core (used for COMPILING tests, as the code uses org.junit.*)
JUNIT4_CORE := $(LIB_DIR)/junit-4.13.2.jar

# HAMCREST Core (required by JUnit 4 Assertions)
HAMCREST := $(LIB_DIR)/hamcrest-core-1.3.jar

# --- Classpaths ---
# CLASSPATH_MAIN is simplified, it doesn't need JUnit for main sources
CLASSPATH_MAIN := $(OUT_MAIN)

# CLASSPATH_TEST needs main compiled classes, JUnit 4, and Hamcrest to compile tests
CLASSPATH_TEST := $(OUT_MAIN);$(JUNIT4_CORE);$(HAMCREST)

# CLASSPATH_RUN needs main and test compiled classes for execution via JUnit Platform
CLASSPATH_RUN := $(OUT_MAIN);$(OUT_TEST)

# ====================================================================
# Targets
# ====================================================================

.PHONY: all clean test compile compile-main compile-test help deps

help:
	@echo ""
	@echo "Available targets:"
	@echo "  helpShow this help message"
	@echo "  depsDownload dependencies (JUnit 4, Hamcrest, JUnit 5 Platform)"
	@echo "  compile Compile main and test sources"
	@echo "  compile-mainCompile only main sources"
	@echo "  compile-testCompile only test sources (depends on compile-main)"
	@echo "  testRun JUnit tests"
	@echo "  clean   Remove build output"
	@echo ""


all: compile


compile: deps compile-main compile-test


compile-main:
	@echo "== Compiling main sources =="
	mkdir -p $(OUT_MAIN)
# The main source compilation shouldn't need the classpath at all if your main code has no dependencies.
# If it needs dependencies, use the correct CLASSPATH_MAIN definition.
	javac -d $(OUT_MAIN) $$(find $(SRC_MAIN) -name "*.java")


compile-test: compile-main
	@echo "== Compiling test sources =="
	mkdir -p $(OUT_TEST)
	javac -cp "$(CLASSPATH_TEST)" -d $(OUT_TEST) $$(find $(SRC_TEST) -name "*.java")

	@echo "== Copying test resources =="
	@if [ -d $(RES_TEST) ]; then cp -r $(RES_TEST)/* $(OUT_TEST)/ 2>/dev/null || true; fi


test: compile
	@echo "== Running JUnit tests =="
	java -jar $(JUNIT_PLATFORM) \
   execute \
 --classpath "$(CLASSPATH_RUN)" \
 --scan-classpath

clean:
	rm -rf $(OUT_DIR)


# ====================================================================
# Dependency Downloading Targets
# ====================================================================

deps: $(JUNIT_PLATFORM) $(JUNIT4_CORE) $(HAMCREST)

# Target to create the lib directory
$(LIB_DIR):
	mkdir -p $(LIB_DIR)

# Target to download the JUnit 5 Platform Console JAR
$(JUNIT_PLATFORM): | $(LIB_DIR)
wget -q https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/6.0.1/junit-platform-console-standalone-6.0.1.jar \
 -O $(JUNIT_PLATFORM)
	@echo "Downloaded JUnit Platform Console 6.0.1"

# Target to download the JUnit 4 Core JAR
$(JUNIT4_CORE): | $(LIB_DIR)
wget -q https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar \
 -O $(JUNIT4_CORE)
	@echo "Downloaded JUnit 4.13.2"

# Target to download the Hamcrest Core JAR
$(HAMCREST): | $(LIB_DIR)
wget -q https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar \
 -O $(HAMCREST)
	@echo "Downloaded Hamcrest Core 1.3"
