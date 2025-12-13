package org.apaulino.structurizr.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import io.quarkus.logging.Log;

@Path("/mock")
public class MockServiceResource {

    @GET
    @Path("/order-management/architecture/dsl")
    @Produces(MediaType.TEXT_PLAIN)
    public String getServiceADsl() {
        Log.info("Mock Service A: Returning DSL fragment");

        return """
                user = person "User" "Usuário do sistema de pedidos"

                orderSystem = softwareSystem "Order Management" "Sistema completo de gerenciamento de pedidos e fulfillment" {

                    orderApi = container "Order API" "API REST para gerenciamento de pedidos" "Java 21, Quarkus" {
                        tags "Microservice"

                        orderController = component "Order Controller" "Endpoints REST para CRUD de pedidos" "JAX-RS"
                        orderService = component "Order Service" "Lógica de negócio de pedidos" "CDI Bean"
                        orderRepository = component "Order Repository" "Acesso a dados de pedidos" "Panache Repository"
                        orderMapper = component "Order Mapper" "Conversão DTO/Entity" "MapStruct"

                        orderController -> orderService "Usa"
                        orderService -> orderRepository "Persiste"
                        orderService -> orderMapper "Converte"
                    }

                    orderDb = container "Order Database" "Armazena pedidos, itens e histórico" "PostgreSQL 16" {
                        tags "Database"
                    }

                    orderQueue = container "Order Events" "Fila de eventos de pedidos" "RabbitMQ" {
                        tags "Queue"
                    }

                    orderWorker = container "Order Worker" "Processador assíncrono de pedidos" "Java 21, Quarkus" {
                        tags "Microservice"

                        eventConsumer = component "Event Consumer" "Consome eventos da fila" "SmallRye Reactive Messaging"
                        fulfillmentService = component "Fulfillment Service" "Processa fulfillment do pedido" "CDI Bean"
                        notificationClient = component "Notification Client" "Envia notificações" "REST Client"

                        eventConsumer -> fulfillmentService "Processa"
                        fulfillmentService -> notificationClient "Notifica"
                    }

                    orderCache = container "Order Cache" "Cache de pedidos frequentes" "Redis" {
                        tags "Cache"
                    }

                    orderApi.orderRepository -> orderDb "Lê/Escreve" "JDBC"
                    orderApi.orderService -> orderQueue "Publica eventos" "AMQP"
                    orderApi.orderService -> orderCache "Consulta/Invalida" "Redis Protocol"
                    orderWorker.eventConsumer -> orderQueue "Consome eventos" "AMQP"
                    orderWorker.fulfillmentService -> orderDb "Atualiza status" "JDBC"
                }

                user -> orderSystem.orderApi "Cria e consulta pedidos" "HTTPS/JSON"
                user -> orderSystem.orderApi.orderController "Acessa endpoints" "REST"
                """;
    }

    @GET
    @Path("/inventory-management/architecture/dsl")
    @Produces(MediaType.TEXT_PLAIN)
    public String getServiceBDsl() {
        Log.info("Mock Service B: Returning DSL fragment");

        return """
                user = person "User" "Usuário do sistema de inventário"

                inventorySystem = softwareSystem "Inventory Management" "Sistema de controle de estoque e warehouse" {

                    inventoryApi = container "Inventory API" "API REST para controle de estoque" "Java 21, Quarkus" {
                        tags "Microservice"

                        stockController = component "Stock Controller" "Endpoints de consulta de estoque" "JAX-RS"
                        reservationController = component "Reservation Controller" "Endpoints de reserva" "JAX-RS"
                        stockService = component "Stock Service" "Lógica de estoque" "CDI Bean"
                        reservationService = component "Reservation Service" "Lógica de reservas" "CDI Bean"
                        inventoryRepository = component "Inventory Repository" "Acesso a dados" "Panache Repository"

                        stockController -> stockService "Usa"
                        reservationController -> reservationService "Usa"
                        stockService -> inventoryRepository "Consulta"
                        reservationService -> inventoryRepository "Reserva"
                        reservationService -> stockService "Valida disponibilidade"
                    }

                    inventoryDb = container "Inventory Database" "Armazena produtos, estoque e reservas" "MongoDB" {
                        tags "Database"
                    }

                    cacheLayer = container "Stock Cache" "Cache de níveis de estoque" "Redis Cluster" {
                        tags "Cache"
                    }

                    inventoryEvents = container "Inventory Events" "Stream de eventos de estoque" "Apache Kafka" {
                        tags "Queue"
                    }

                    warehouseSync = container "Warehouse Sync" "Sincronização com WMS" "Java 21, Quarkus" {
                        tags "Microservice"

                        syncScheduler = component "Sync Scheduler" "Agendador de sincronização" "Quarkus Scheduler"
                        wmsClient = component "WMS Client" "Cliente do sistema WMS" "REST Client"
                        reconciliationService = component "Reconciliation Service" "Reconcilia diferenças" "CDI Bean"

                        syncScheduler -> wmsClient "Busca dados"
                        syncScheduler -> reconciliationService "Reconcilia"
                    }

                    inventoryApi.inventoryRepository -> inventoryDb "Lê/Escreve" "MongoDB Driver"
                    inventoryApi.stockService -> cacheLayer "Cache de leitura" "Redis Protocol"
                    inventoryApi.reservationService -> inventoryEvents "Publica reservas" "Kafka Producer"
                    warehouseSync.reconciliationService -> inventoryDb "Atualiza estoque" "MongoDB Driver"
                    warehouseSync.reconciliationService -> inventoryEvents "Publica atualizações" "Kafka Producer"
                }

                user -> inventorySystem.inventoryApi "Consulta disponibilidade" "HTTPS/JSON"
                user -> inventorySystem.inventoryApi.stockController "Verifica estoque" "REST"
                user -> inventorySystem.inventoryApi.reservationController "Reserva produtos" "REST"
                """;
    }
}
