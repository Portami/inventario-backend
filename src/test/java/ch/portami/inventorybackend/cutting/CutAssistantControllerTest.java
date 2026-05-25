package ch.portami.inventorybackend.cutting;

import ch.portami.inventorybackend.BaseIntegrationTest;
import ch.portami.inventorybackend.cutassistant.CuttingStockLoader;
import ch.portami.inventorybackend.cutassistant.api.dto.CutProposalDto;
import ch.portami.inventorybackend.cutassistant.api.dto.RequestCutProposalsDto;
import ch.portami.inventorybackend.cutassistant.domain.CuttableStock;
import ch.portami.inventorybackend.cutassistant.domain.StockType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class CutAssistantControllerTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;
    private String baseUrl;

    @MockitoBean
    private CuttingStockLoader cuttingStockLoader;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void givenFeltTypeAndPieces_whenRequestProposals_thenReturns200AndProposalList() {
        Long feltId = 1L;
        String feltType = "Wool";
        
        // Mock inventory so the optimizer has material to cut from
        CuttableStock largeRoll = new CuttableStock(StockType.ROLL, feltId, feltType, 500.0, 500.0);
        when(cuttingStockLoader.loadAll()).thenReturn(List.of(largeRoll));

        var piece = new RequestCutProposalsDto.RequestedPieceDto(100, 50, 1);
        var requestDto = new RequestCutProposalsDto(feltType, List.of(piece));
        String url = baseUrl + "/api/v1/cut-assistant/proposals";

        ResponseEntity<CutProposalDto[]> response = restTemplate.postForEntity(url, requestDto, CutProposalDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void givenScrapAndRollAvailable_whenRequestFitsOnScrap_thenProposalPrioritizesScrapSource() {
        Long feltId = 1L;
        String feltType = "Wool";
        String url = baseUrl + "/api/v1/cut-assistant/proposals";

        CuttableStock largeRoll = new CuttableStock(StockType.ROLL, feltId, feltType, 500.0, 500.0);
        CuttableStock smallScrap = new CuttableStock(StockType.SCRAP, feltId, feltType, 120.0, 120.0);
        
        when(cuttingStockLoader.loadAll()).thenReturn(List.of(largeRoll, smallScrap));

        var piece = new RequestCutProposalsDto.RequestedPieceDto(100, 100, 1);
        var requestDto = new RequestCutProposalsDto(feltType, List.of(piece));

        ResponseEntity<CutProposalDto[]> response = restTemplate.postForEntity(url, requestDto, CutProposalDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CutProposalDto[] proposals = response.getBody();
        assertThat(proposals).isNotNull();
        assertThat(proposals.length).isGreaterThan(0);
        
        assertThat(proposals[0].proposedCuts()).hasSize(1);
        assertThat(proposals[0].proposedCuts().get(0).sourceStockId()).isNotNull();
    }
}
