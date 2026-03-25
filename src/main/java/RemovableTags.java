public final class RemovableTags {

    private RemovableTags() {}

    public static final String SELECTORS = String.join(", ",

            "nav", "footer", "header", "aside", "menu", "menuitem",

            "script", "noscript", "style", "link[rel=stylesheet]",

            "iframe", "embed", "object", "video", "audio", "source",
            "picture", "canvas", "svg",

            "form", "input", "textarea", "select", "button", "label",
            "fieldset", "legend", "datalist", "output",

            "[class*=ad]", "[id*=ad]", "[class*=banner]", "[class*=sponsor]",
            "[class*=promo]", "[class*=cookie]", "[class*=popup]",
            "[class*=modal]", "[class*=overlay]", "[aria-hidden=true]"
    );
}

