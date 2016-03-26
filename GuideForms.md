# Pectin Forms #

**TODO: Give a nice easy overview to forms...**

Read more about [the basics of forms](GuideFormBasics.md).

## Plugins ##
Plugins allow you to decorate the functionality of forms in a business style language.  Pectin provides two plugins, one for [validation](GuideValidationPlugin.md) and one for [metadata](GuideMetadataPlugin.md) (enabled, visible & watermarks).

For example, if your forms required obfucated fields (such as limiting the display of credit card numbers or account details) you would create a plugin that provides an `obfuscate(creditCard).when(...)` style methods and bindings to support it.  This way the complex behaviour required for obfuscation is contained within the plugin and there's no need to worry that every developer has implemented obfuscation correctly (the DRY principle).

By using this approach Pectin doesn't force you to use a one-size-fits-all solution.  Plugins are accessed using static methods so they won't clutter the API if you're not using them.  If you don't like the plugins provided or need more advanced functionality you can develop your own (or feel free to hire me to do it for you).