# Sloth

##### The repository backing [gridr.io](https://www.gridr.io)

## Local development setup
- Get a local transactor running from the `datomic` directory: `bin/transactor config/sloth-dev-transactor.properties`.
- To get a clj/cljs repl running, from `dev/user.clj`, do `cider-jack-in-clojurescript` via Cider.
- Run `lein garden auto` to update styles automatically.

## Development workflow
- Start a repl, and do `(go)`.
- After adding/changing code, do `(reset)` to see those changes.
- In case of compile errors, the `user` namespace might not load properly, in this case, you can do `(repl/refresh)` then `(go)`.
