package com.example.projindividual;

import android.provider.BaseColumns;

public final class AppHelper {
 // CLASSE PARA AUXILIO DA DATABASE
    private AppHelper() {

    }

    //NOME DAS COLUNAS E DA RESPETIVA TABELA
    public static class CenarioTable implements BaseColumns {
        public static final String TABLE_NAME = "app_cenarios";
        public static final String COLUMN_CENARIO = "cenario";
        public static final String COLUMN_PERGUNTA = "pergunta";
        public static final String COLUMN_RESPOSTA1 = "resposta1";
        public static final String COLUMN_RESPOSTA2 = "resposta2";
        public static final String COLUMN_RESPOSTA3 = "resposta3";
        public static final String COLUMN_RESPOSTA4 = "resposta4";
        public static final String COLUMN_CENA_NR = "nr_cenario";
        public static final String COLUMN_CORRECTA = "resp_correcta";

    }

    public static class UserTable implements BaseColumns {
        public static final String TABLE_NAME_USR = "app_user";
        public static final String COLUMN_NAME_USR = "username";
        public static final String COLUMN_PW_USR = "password";
        public static final String COLUMN_RESP_ANT1 = "resp_anterior1";
        public static final String COLUMN_RESP_ANT2 = "resp_anterior2";
        public static final String COLUMN_RESP_ANT3 = "resp_anterior3";
    }


}