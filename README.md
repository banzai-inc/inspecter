# Inspecter

Clojure library offering a few helpful extensions to the
popular [Specter](https://github.com/redplanetlabs/specter)
for selecting and transforming [Hiccup](https://github.com/weavejester/hiccup) data using
simple CSS selectors.

## Installation
First add [Specter](https://github.com/redplanetlabs/specter) as a dependency to
your project. Then add the following dep to your project or deps file:

[TBD]

## Usage

First be sure to include the relevant libraries:

```clojure
(require '[com.rpl.specter :as specter])
(require '[inspecter.core :as i])
```

### select

Find a Hiccup element by its id:

```clojure
(def hiccup
  [:div
   [:h1#zero "My Title"]
   [:h2#one {:data-attr "one"} "Hello"]
   [:h2#two {:data-attr "two"} "World"]])

user=> (specter/select [(i/matches :#two)])
[[:h2#two {:data-attr "one"} "World"]]
```

Specter's `select` function returns a vector of _ALL_ items that match the selector.

Push it a little further and grab the attribute maps for every `h2`:

```clojure
user=> (specter/select [(i/matches :h2) i/ATTRS])
[{:data-attr "one"}
 {:data-attr "two"}]
```

### transform

Most importantly, though, is Specter's ability to navigate to items in collections and update 
them in place. This library makes updating Hiccup in place easy:

```clojure
(def hiccup
  [:div
   [:h1 "My Title"]
   [:h2.find-me "My Subtitle"]])

user=> (specter/transform
         [(inspect/matches :.find-me) inspect/ATTRS]
         (inspect/update-attrs #(assoc % :changed :me))
         hiccup)
[:div
 [:h1 "My Title"]
 [:h2.find-me {:changed :me} "My Subtitle"]]
```

### Complete Function List

Inspecter exposes the following functions for Hiccup path navigation and transformation:

- `inspecter.core/matches` - Returns a recursive path navigator
- `inspecter.core/ATTRS` - Path navigator to an element's attributes.
- `inspecter.core/CONTENTS` - Path navigator to an element's contents.
- `inspecter.core/update-attrs` - Helper for updating an element's attributes.

### CSS Selectors

Inspecter matches Hiccup elements via simple, limited-scope CSS selectors. While the 
[universe of potential CSS selectors](https://www.w3.org/TR/selectors-4/) is enormous, 
Inspecter implements a narrow set for **tag**, **id**, and **class** matching. (I'm considering 
adding support for matching on attributes.). e.g. These are each supported:

* `:div` - Matches all `[:div ...]` elements
* `:nav#main` - Matches all `[:nav {:id "main"} ...]` or `[:nav#main ...]`
* `:div.small.button` - Matches all `[:div.small.button]` or `[:div {:class "small button"} ...]` elements
 
## License

Distributed under the [MIT License](https://github.com/banzai-inc/inspecter/blob/main/LICENSE).
