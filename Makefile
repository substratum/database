test:
	@find . -type f -iname '*.xml' -exec xmlstarlet val {} \;

.PHONY: test