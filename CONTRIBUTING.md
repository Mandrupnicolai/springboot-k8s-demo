# Contributing

Thank you for your interest in contributing!

## Ground rules

- Open an issue **before** starting significant work.
- One feature / fix per pull request.
- All PRs must pass CI (build, tests, coverage gate, Helm lint).
- Keep commits atomic and write meaningful commit messages using [Conventional Commits](https://www.conventionalcommits.org/).

## Local setup

```bash
git clone https://github.com/YOUR_GITHUB_USERNAME/springboot-k8s-demo.git
cd springboot-k8s-demo
mvn verify          # build + test + coverage
```

## Branch strategy

| Branch | Purpose |
|---|---|
| `main` | Production — protected, requires PR + review |
| `feature/*` | New features |
| `fix/*` | Bug fixes |
| `release/*` | Release candidates |

## Code style

- Follow standard Java conventions (Google Java Style).
- No magic strings — use constants or environment variables.
- All new code requires unit tests; integration tests where appropriate.
- Coverage must stay = 80 %.
