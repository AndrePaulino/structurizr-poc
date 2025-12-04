workspace "Structurizr POC" "Documentação de Arquitetura usando C4 Model" {

    !identifiers hierarchical

    model {
        u = person "User" "Usuário que acessa a documentação de arquitetura"
        
        ss = softwareSystem "Structurizr POC" "Serviço Quarkus que serve documentação de arquitetura como diagramas C4" {
            wa = container "Quarkus Application" "Aplicação REST que serve os diagramas de arquitetura" "Java 21, Quarkus 3.x" {
                tags "Application"
            }
            dsl = container "DSL Workspace" "Definição da arquitetura em Structurizr DSL" "Structurizr DSL" {
                tags "Documentation"
            }
            static = container "Static HTML Site" "Site estático gerado com diagramas C4 interativos" "HTML, JavaScript, CSS" {
                tags "WebBrowser"
            }
        }

        u -> ss.wa "Acessa diagramas de arquitetura via HTTP"
        ss.dsl -> ss.static "Gera durante build (Structurizr CLI)"
        ss.wa -> ss.static "Serve arquivos estáticos"
    }

    views {
        systemContext ss "SystemContext" {
            include *
            autolayout lr
        }

        container ss "Containers" {
            include *
            autolayout lr
        }

        styles {
            element "Element" {
                color #ffffff
                stroke #0773af
                strokeWidth 7
                shape roundedbox
            }
            element "Person" {
                background #08427B
                shape person
            }
            element "Software System" {
                background #1168BD
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
                thickness 3
                color #707070
            }
        }
    }

    configuration {
        scope softwaresystem
    }

}
