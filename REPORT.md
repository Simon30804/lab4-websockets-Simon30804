# Lab 4 WebSocket -- Project Report

## Description of Changes
- Actualizado ComplexClient, para que después de recibir el mensaje inicial "The doctor is in", envía "Im feeling sad." al servidor.
- Completado el test onChat, mediante el uso de assertTrue(size in 4..5) en lugar de usar assertEquals, ya que el orden/timing de mensajes puede variar según la máquina.
- Verificamos que el primer mensaje es correcto (list[0] == "The doctor is in.").


## Technical Decisions
- Rango 4..5 en la aserción, piues el mínimo de mensajes garantizado son 3 iniciales + 1 respuesta, con la posibilidad de un quinto mensaje adicional por variación temporal en el servidor.

## Learning Outcomes
- Uso práctico de WebSocket en Spring Boot 
- Control de sincronización en test concurrentes con CountDownLatch
- Diferencias entre pruebas deterministas, donde podemos hacer uso de assertEquals, y pruebas tolerantes a concurrencia, donde hacemos uso de aserciones sobre rango mediante assertTrue.
- Comportamiento de un bot tipo Eliza y su comportamiento basado en patrones

## AI Disclosure
### AI Tools Used
- [List specific AI tools used]

### AI-Assisted Work
- [Describe what was generated with AI assistance]
- [Percentage of AI-assisted vs. original work]
- [Any modifications made to AI-generated code]

### Original Work
- [Describe work done without AI assistance]
- [Your understanding and learning process]