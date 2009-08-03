package droolsbook.etl.service.impl;

import static org.junit.Assert.*;

import java.io.Reader;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.junit.Before;
import org.junit.Test;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import droolsbook.bank.service.ReportFactory;
import droolsbook.bank.service.impl.DefaultReportFactory;
import droolsbook.transform.service.DataTransformationService;
import droolsbook.transform.service.LegacyBankService;
import droolsbook.transform.service.impl.DataTransformationServiceImpl;
import droolsbook.transform.service.impl.IBatisLegacyBankService;
import droolsbook.utils.DroolsHelper;

public class ETLBankServiceImplTest {

  DataTransformationServiceImpl etlBankService;
  
  @Before
  public void setUp() throws Exception {

    Reader reader = Resources
        .getResourceAsReader("SqlMapConfig.xml");
    SqlMapClient sqlMapClient = SqlMapClientBuilder
        .buildSqlMapClient(reader);
    reader.close();

    LegacyBankService legacyBankService = new IBatisLegacyBankService(
        sqlMapClient);

    KnowledgeBase knowledgeBase = DroolsHelper
        .createKnowledgeBase("etl-iBatis.drl");

    etlBankService = new DataTransformationServiceImpl();
    //etlBankService.setBankingService(new BankingServiceImpl());
    etlBankService.setLegacyBankService(legacyBankService);
    etlBankService.setReportFactory(new DefaultReportFactory());
    etlBankService.setKnowledgeBase(knowledgeBase);
  }

  @Test
  public void testEtl() {
    //TODO: setup your database before enabling this test
    //etlBankService.etl();
  }

}
