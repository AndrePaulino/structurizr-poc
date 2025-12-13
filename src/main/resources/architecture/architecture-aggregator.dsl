workspace "Architecture Aggregator" "Documentação de Arquitetura do Serviço Agregador - C4 Model" {

    !identifiers hierarchical

    model {
        # Personas
        developer = person "Developer" "Desenvolvedor que consulta a documentação de arquitetura"
        devops = person "DevOps Engineer" "Engenheiro responsável pela infraestrutura e deploy"
        
        # Sistema Principal - Architecture Aggregator
        aggregator = softwareSystem "Architecture Aggregator" "Serviço Quarkus que agrega e serve documentação de arquitetura de múltiplos microsserviços como diagramas C4 interativos" {
            
            api = container "REST API" "API REST que expõe endpoints para geração e consulta de diagramas" "Java 21, Quarkus 3.x, JAX-RS" {
                tags "Application"
                
                generatorResource = component "GeneratorResource" "Endpoint para disparar geração de sites estáticos" "JAX-RS Resource"
                architectureResource = component "ArchitectureResource" "Endpoint para servir páginas de arquitetura" "JAX-RS Resource"
                mockResource = component "MockServiceResource" "Endpoints mock para desenvolvimento" "JAX-RS Resource"
            }
            
            services = container "Domain Services" "Serviços de domínio para processamento de DSL e geração de sites" "Java 21" {
                tags "Application"
                
                registry = component "ServiceRegistry" "Registro de serviços externos que fornecem DSL" "CDI Bean"
                generator = component "StaticSiteGeneratorService" "Processa DSL e gera workspace.js com autolayout" "CDI Bean"
                writer = component "StaticFileWriterService" "Persiste arquivos HTML e JS gerados" "CDI Bean"
            }
            
            clients = container "REST Clients" "Clientes HTTP para buscar DSL de serviços externos" "MicroProfile REST Client" {
                tags "Application"
                
                serviceAClient = component "ServiceAClient" "Cliente para Service A" "REST Client"
                serviceBClient = component "ServiceBClient" "Cliente para Service B" "REST Client"
            }
            
            staticSite = container "Static Site" "Sites estáticos gerados com diagramas C4 interativos por serviço" "HTML, JavaScript, CSS" {
                tags "WebBrowser"
            }
            
            dslFiles = container "DSL Files" "Arquivos de definição da arquitetura do próprio agregador" "Structurizr DSL" {
                tags "Documentation"
            }
        }
        
        # Sistemas Externos (Microsserviços que fornecem DSL)
        externalServices = group "External Microservices" {
            serviceA = softwareSystem "Order Management Service" "Microsserviço de gestão de pedidos que expõe seu DSL de arquitetura" {
                tags "External"
            }
            serviceB = softwareSystem "Inventory Service" "Microsserviço de inventário que expõe seu DSL de arquitetura" {
                tags "External"
            }
        }
        
        # Infraestrutura
        graphviz = softwareSystem "Graphviz" "Motor de layout automático para posicionamento de elementos nos diagramas" {
            tags "Infrastructure"
        }

        # Relacionamentos - Personas
        developer -> aggregator.api "Consulta diagramas de arquitetura"
        developer -> aggregator.staticSite "Visualiza diagramas interativos"
        devops -> aggregator.api "Dispara geração de sites"
        
        # Relacionamentos - API -> Services
        aggregator.api.generatorResource -> aggregator.services.registry "Obtém lista de serviços"
        aggregator.api.generatorResource -> aggregator.services.generator "Solicita geração"
        aggregator.api.generatorResource -> aggregator.services.writer "Solicita persistência"
        aggregator.api.architectureResource -> aggregator.services.registry "Consulta serviços"
        aggregator.api.architectureResource -> aggregator.services.generator "Gera workspace.js"
        
        # Relacionamentos - Services -> Clients
        aggregator.services.registry -> aggregator.clients "Busca DSL via REST Clients"
        aggregator.clients.serviceAClient -> serviceA "GET /architecture/dsl" "HTTP/REST"
        aggregator.clients.serviceBClient -> serviceB "GET /architecture/dsl" "HTTP/REST"
        
        # Relacionamentos - Generator -> Graphviz
        aggregator.services.generator -> graphviz "Aplica autolayout"
        
        # Relacionamentos - Writer -> Static Site
        aggregator.services.writer -> aggregator.staticSite "Escreve arquivos gerados"
        
        # Build-time
        aggregator.dslFiles -> aggregator.staticSite "Gera durante build (Structurizr CLI)"
    }

    views {
        systemLandscape "Landscape" "Visão geral do ecossistema" {
            include *
            autolayout lr
        }
        
        systemContext aggregator "SystemContext" "Contexto do Architecture Aggregator" {
            include *
            autolayout lr
        }

        container aggregator "Containers" "Visão de containers do Architecture Aggregator" {
            include *
            autolayout lr
        }
        
        component aggregator.api "APIComponents" "Componentes da REST API" {
            include *
            autolayout lr
        }
        
        component aggregator.services "ServiceComponents" "Componentes dos Domain Services" {
            include *
            autolayout lr
        }

        styles {
            element "Element" {
                color #ffffff
            }
            element "Person" {
                background #08427B
                shape person
            }
            element "Software System" {
                background #1168BD
            }
            element "Container" {
                background #438DD5
            }
            element "Component" {
                background #85BBF0
                color #000000
            }
            element "Database" {
                shape cylinder
            }
            element "Queue" {
                shape pipe
            }
            element "External" {
                background #999999
            }
            element "Infrastructure" {
                background #6B8E23
            }
            element "Application" {
                background #438DD5
            }
            element "Documentation" {
                background #85BBF0
                color #000000
            }
            element "WebBrowser" {
                background #438DD5
                shape webbrowser
            }
            relationship "Relationship" {
                color #707070
            }
        }
    }

    configuration {
        scope softwaresystem
    }
}
