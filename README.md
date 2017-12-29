# Sloth

##### The repository backing gridr.io

## Local development setup
- To get a clj/cljs repl running, from `dev/user.clj`, do `cider-jack-in-clojurescript` via Cider.
- Run `lein garden auto` to update styles automatically.
- To build, run `lein build`.

## Development workflow
- Start a repl, and do `(go)`.
- After adding/changing code, do `(reset)` to see those changes.
- In case of compile errors, the `user` namespace might not load properly, in this case, you can do `(repl/refresh)` then `(go)`.
