ATGest
======

**ATGest** é uma aplicação para agendar assistências técnicas


Utilizador Exemplo:
    Email: andre@zonaporto.com
    Password: 123456
==============================


Funcionalidades
---------------

Trabalhar com a API que está no endereço zonaporto.com/api/

*Autenticação*

    Autenticar utilizador e guardar o seu Id, Nome, Email, Foto de perfil e API Token na base de dados local
    Logout elimina todos os dados da base de dados local


*Sincronização*

    Sincronizar clientes da base de dados local com a API


*Clientes*

    Ver a lista de clientes e filtrar por nome ou por código de cliente
    Editar cliente e guardar alterações na base de dados


*Assistências Técnicas*

    Agendar nova assistência e guardar os dados na base de dados local
    Editar assistência e guardar as alterações na base de dados local


*Colaboradores*

    Mostra a localização atual no google maps.


 Bugs
 ----

 O calendário de visualização das assistências técnicas, inicialmente, mostra-se uma semana antes do dia atual.
 Permite criar assistências técnicas onde a hora de fim é inferior ou igual à hora de início.


 To Do
 -----
 Melhorar o processo de sincronização de clientes

 Sincronizar assistências técnicas (As ATs não sincronizadas e não sincronizadas terão cores diferentes)

 Mostrar a localização dos utilizadores da APP no ecrã "Colaboradores"

 Atividades de background para sincronização automática

 Notifização de assistência técnica a decorrer

 Adicionar funcionalidade de associar assinatura do cliente à assistência técnica (A API já permite)

 Adicionar funcionalidade de associar anexos à assistência técnica (A API já permite)