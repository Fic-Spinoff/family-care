# Issues
1. Un reviewer crea el issue y lo asigna a una persona, o bien, la persona implicada crea el issue con una descripción detallada y se lo asigna.
1. Antes de hacer nada esa persona lo lee y comenta cualquier duda por telegram. Resueltas las dudas apunta todo lo hablado y necesario en un comentario en el issue.
1. Cuando se comente "Aceptado" y se marque con dicha etiqueta se dará por terminada la discusión y se podrá comenzar.

# Branching
1. Crear rama en local con nombre: issue-X (X: código del issue, sin ceros a la izquierda)
1. Trabajar en local, hacer los commits que se necesite, etc.
1. Cuando se vaya a subir su rama al repositorio remoto por primera vez:
	A. Hacer un build y ver que funciona.
	B. Arreglar errores Lint.
	C. Hacer pull del master.
	D. Hacer un rebase de su rama sobre master fusionando todos sus commits en 1 (si tiene más de 1).
	E. Hacer build y ver que todo funciona correcto. Si no arreglar, commitear y rebase de nuevo sobre su propia rama para fusionar último commit.
	F. Push
1. Para subir nuevos cambios de una rama ya publicada al repositorio hay 2 opciones.
	- Pull de la rama por si hay cambios remotos, merge  y push.
	- Pull de la rama por si hay cambios. Rebase únicamente de tus cambios locales sobre tu propia rama (fusionando los commits en 1) y push.

# Pull Requests
A continuación se describe el uso de los PR, cuando crearlos, algunas instrucciones obligatorias y situaciones frecuentes.

## ¿Cuándo crear un PR?
Un PR siempre irá asociado una issue y se podrá crear en los siguiente casos.
* Cuando una tarea esté lista para mergear. Hay que definir que es listo para mergear, por de pronto la rama tiene que pasar los checks de CI.
* Cuando durante el desarrollo de una tarea nos encontremos con dificultades, podemos crear un PR antes de que esté listo para mergear, para añadir comentarios en el código, etc.

## Proceso de un PR
* Crear con la rama implicada desde sobre el master.
* La descripción del PR debe comenzar por: "This PR resolves #X", siendo X el número de la issue a la que hace referencia.
* A continuación se describirán los cambios realizados en el código con el detalle que se considere necesario.
* Algún reviewer revisará el PR y solicitará los cambios pertientes.
* Cuando se considere necesario se aprobará y mergeará el PR.

# FAQ

Respuestas a algunas preguntas que pueden surgir durante el uso de git y la aplicación de las anteriores pautas.

## ¿Por qué subir tu rama al repositorio remoto?
* Si tienes un problema que no puedes solucionar y necesitas ayuda. Si el problema impide un build correcto entonces omitir pasos A y B.
* Si estás trabajando con alguien más en esa tarea y quieres subir tus cambios. En ese caso no hacer rebase (ver: ¿Cuándo NO hacer rebase?)y hacer siempre un pull de la rama antes.
* Si no vas a poder seguir trabajando en esa tarea y alguien debe continuarla por ti.
* Si crees que la tarea está para mergear y vas a crear un PR.

## ¿Cuándo NO hacer rebase?
Rebase es una operación peligrosa si no se usa adecuadamente. Como se explica anteriormente un rebase reescribe la historia de la rama lo que implica que los commits existentes se pueden ver
alterados. Por eso nunca se debe hacer un rebase de una rama pública (que se enccuentra en el repositorio remoto).
* Si más de una persona trabaja con esa rama. Podríamos destruir los commits de esas personas haciendo que su versión de la rama y la nuestra no tengan ningún punto en común.
* Si la rama se encuentra en el repositorio remoto. Cualquier persona podría tener esa rama en local y al hacer un rebase eliminar el punto común de esa rama local. Se podría tomar una 
excepción, si estamos seguros de que solo nosotros trabajamos con esa rama y existe algún motivo importante para hacer el rebase. Por norma general NO.
TL;DR: No hacer rebase de una rama si esta existe en cualquier otro sitio que no sea tu repositorio local (repositorio remoto, repositorio local de otro usuario, etc.).

## Mi rama es pública y necesito hacer un rebase.
Si te has vuelto un loco del rebase porque te has aficionado a dejar la historia lo más limpia posible y tu rama ya se ha publicado... Todo tiene solución.

Puedes crear una rama local desde esa misma rama y hacer rebase de ésta.
Si ya has creado un PR de la rama antigua:
1. Cierra el PR o solicita el cierre con un mensaje en el que indicas este motivo.
1. Crea un nuevo PR siguiendo el mismo proceso que en el anterior para la nueva rama.

# Comandos
Información de utilidad sobre algunos comandos.
## Rebase
Leer la guía de [Atlassian](https://www.atlassian.com/git/tutorials/rewriting-history/git-rebase)
* `git rebase -i HEAD^3`
 * Este comando permite reescribir la historia de los últimos 3 commits (^3).
 * "-i" para utilizar el modo interactivo. 
* `git rebase master`
* `p`: Pick, los commits marcados con pick se mantendrán en la historia
* `s`: Squash, los commits marcados con squash desaparecerán y se unificaran con el siguiente pick.
