.PHONY: all tools build clean install run

PROJECT_NAME := daybreak
MAIN_NAMESPACE := $(PROJECT_NAME)
M2_DIR := .m2
TOOLS_BIN_DIR := ${HOME}/.local/bin


all: runtime build

runtime:
	@if command -v apt-get &> /dev/null; then \
		sudo apt-get update; \
		sudo apt-get install -y \
			curl \
			openjdk-21-jdk; \
	fi
	@mkdir -p $(TOOLS_BIN_DIR)
	@if [ -z $(shell which bb) ]; then \
		curl -s https://raw.githubusercontent.com/babashka/babashka/master/install | \
		bash -s -- --dir $(TOOLS_BIN_DIR); \
	fi; \
	curl --version
	bb describe
	java -version

build: $(M2_DIR)

run: $(M2_DIR)
	bb -m $(MAIN_NAMESPACE)

clean:
	rm -rf $(M2_DIR)

install:
	echo "Not implemented"
	exit 1

$(M2_DIR): $(ENTRYPOINT)
	bb -m $(MAIN_NAMESPACE) --only-download-deps 
