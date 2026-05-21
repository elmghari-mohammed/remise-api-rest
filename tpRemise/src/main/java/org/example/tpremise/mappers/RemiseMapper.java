package org.example.tpremise.mappers;

import org.example.tpremise.models.Remise;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RemiseMapper implements RowMapper<Remise> {
    @Override
    public Remise mapRow(ResultSet rs, int rowNum) throws SQLException {
            Remise remise = new Remise();
            remise.setId(rs.getLong("id"));
            remise.setMontantMin(rs.getDouble("montant_min"));
            remise.setMontantMax(rs.getDouble("montant_max"));
            remise.setTaux(rs.getDouble("taux"));
            return remise;
    }
}
