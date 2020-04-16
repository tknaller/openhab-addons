/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.bsblan.internal.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.Test;
import org.openhab.binding.bsblan.internal.api.dto.BsbLanApiParameterQueryResponseDTO;
import org.openhab.binding.bsblan.internal.api.dto.BsbLanApiParameterSetRequestDTO;
import org.openhab.binding.bsblan.internal.api.dto.BsbLanApiParameterSetRequestDTO.Type;
import org.openhab.binding.bsblan.internal.api.dto.BsbLanApiParameterDTO;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

/**
 * The {@link BsbLanApiContentConverterTests} class implements tests
 * for {@link BsbLanApiContentConverter}.
 *
 * @author Peter Schraffl - Initial contribution
 */
@NonNullByDefault
public class BsbLanApiContentConverterTests {

    @Test
    public void parseBsbLanApiParameterQueryResponse() {
        String content =
            "{\r\n" +
                "\"700\": {\r\n" +
                    "\"name\": \"Betriebsart\",\r\n" +
                    "\"value\": \"0\",\r\n" +
                    "\"unit\": \"\",\r\n" +
                    "\"desc\": \"Schutzbetrieb\",\r\n" +
                    "\"dataType\": 1\r\n" +
                "}\r\n" +
            "}";

        BsbLanApiParameterQueryResponseDTO r = BsbLanApiContentConverter.fromJson(content, BsbLanApiParameterQueryResponseDTO.class);
        assertNotNull(r);
        assertTrue(r.containsKey(700));

        BsbLanApiParameterDTO p = r.get(700);
        assertEquals("Betriebsart", p.name);
        assertEquals("0", p.value);
        assertEquals("", p.unit);
        assertEquals("Schutzbetrieb", p.description);
        assertEquals(BsbLanApiParameterDTO .DataType.DT_ENUM, p.dataType);
    }

    @Test
    public void serializeBsbLanApiParameterSetRequest() {
        BsbLanApiParameterSetRequestDTO request = new BsbLanApiParameterSetRequestDTO();
        request.parameter = "1234";
        request.value = "Hello World";
        request.type = Type.SET;

        String serializedRequest = BsbLanApiContentConverter.toJson(request);

        // verify serialized content
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(serializedRequest).getAsJsonObject();

        // Although specifying the parameter as int (which would be nicer) also seems to work,
        // we use a String here as this is the way it is noted in the documentation.
        // So ensure there is a 'Parameter' and it is serialized as string.
        assertEquals("1234", json.get("Parameter").getAsString());

        // ensure there is a 'Value' and it is serialized as string
        assertEquals("Hello World", json.get("Value").getAsString());

        // ensure there is a 'Type' and it is serialized as number
        assertEquals(1, json.get("Type").getAsInt());
    }
}
