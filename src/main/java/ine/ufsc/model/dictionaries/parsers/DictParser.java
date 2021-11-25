/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ine.ufsc.model.dictionaries.parsers;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Gabriel
 */
public interface DictParser {

    public void doParsing(ArrayList<File> files) throws IOException, SQLException;
}
