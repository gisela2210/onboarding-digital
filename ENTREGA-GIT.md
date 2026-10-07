# Entrega en Git — rama `desarrolloca`

El requisito de la evaluación indica que el código debe encontrarse en la rama **`desarrolloca`**.

> Una rama pertenece al repositorio Git y no puede quedar garantizada solamente por el contenido de un ZIP sin su historial `.git`. Por ello, después de descomprimir/copiar el proyecto al repositorio privado, deben ejecutarse los siguientes comandos.

## Primera vez

```bash
git checkout -b desarrolloca
git add .
git commit -m "Entrega Sistema de Onboarding Digital"
git push -u origin desarrolloca
```

## Si `desarrolloca` ya existe

```bash
git checkout desarrolloca
git pull origin desarrolloca
git add .
git commit -m "Actualización entrega Onboarding Digital"
git push origin desarrolloca
```

## Comprobar antes de entregar

```bash
git branch --show-current
git status
git log -1 --oneline
```

`git branch --show-current` debe devolver:

```text
desarrolloca
```

Finalmente, en GitHub debe seleccionarse la rama `desarrolloca` y comprobar que contiene `backend`, `frontend`, `database`, `docs`, `docker-compose.yml` y `README.md`.
