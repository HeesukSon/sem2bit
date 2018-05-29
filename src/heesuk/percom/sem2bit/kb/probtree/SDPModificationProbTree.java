package heesuk.percom.sem2bit.kb.probtree;

import heesuk.percom.sem2bit.kb.protocol.enums.Functionality;
import heesuk.percom.sem2bit.kb.protocol.enums.MessageFieldType;
import heesuk.percom.sem2bit.kb.protocol.enums.RequirementChange;
import heesuk.percom.sem2bit.kb.protocol.enums.UpdatePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDPModificationProbTree extends ModificationProbTree{
	private static final Logger LOG = LoggerFactory.getLogger(SDPModificationProbTree.class);

	public SDPModificationProbTree(){
		super();
	}

	public void addTreeNodes() {
		// Depth 1 -> Depth 2
		root.addChild(Functionality.SERVICE_SEARCH.toString());
		root.addChild(Functionality.CONTENT_PARSING.toString());
		root.addChild(Functionality.MESSAGE_HANDLING.toString());

		// Depth 2 -> Depth 3
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.addChild(RequirementChange.MULTI_QUERY_SUPPORT.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.addChild(RequirementChange.CONTENT_LENGTH_CHANGE.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.addChild(RequirementChange.ENCODING_INTEGRATION.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.addChild(RequirementChange.MULTI_ENCODING_SUPPORT.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.addChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString());

		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.addChild(RequirementChange.SECURITY_REQUIREMENT_CHANGE.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.addChild(RequirementChange.CONTROL_OPTION_ADDITION.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.addChild(RequirementChange.SESSION_MGMT_CHANGE.toString());

		// Depth 3 -> Depth 4
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.addChild(MessageFieldType.QUERY_COUNT.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.addChild(MessageFieldType.ANSWER_COUNT.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.CONTENT_LENGTH_CHANGE.toString())
				.addChild(MessageFieldType.MESSAGE_LENGTH.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.ENCODING_INTEGRATION.toString())
				.addChild(MessageFieldType.ENCODING.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_ENCODING_SUPPORT.toString())
				.addChild(MessageFieldType.ENCODING.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.addChild(MessageFieldType.LANGUAGE_CODE.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.addChild(MessageFieldType.LANGUAGE_TAG.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.addChild(MessageFieldType.LANGUAGE_TAG_LENGTH.toString());

		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.SECURITY_REQUIREMENT_CHANGE.toString())
				.addChild(MessageFieldType.CONTROL_FLAG.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString())
				.addChild(MessageFieldType.CONTROL_FLAG.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.SESSION_MGMT_CHANGE.toString())
				.addChild(MessageFieldType.SESSION_MGMT.toString());

		// Depth 4 -> Depth 5
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.getChild(MessageFieldType.QUERY_COUNT.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.getChild(MessageFieldType.QUERY_COUNT.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.getChild(MessageFieldType.QUERY_COUNT.toString())
				.addChild(UpdatePattern.CHANGE_FIELD_LENGTH.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.getChild(MessageFieldType.ANSWER_COUNT.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.getChild(MessageFieldType.ANSWER_COUNT.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_QUERY_SUPPORT.toString())
				.getChild(MessageFieldType.ANSWER_COUNT.toString())
				.addChild(UpdatePattern.CHANGE_FIELD_LENGTH.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.CONTENT_LENGTH_CHANGE.toString())
				.getChild(MessageFieldType.MESSAGE_LENGTH.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.CONTENT_LENGTH_CHANGE.toString())
				.getChild(MessageFieldType.MESSAGE_LENGTH.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.CONTENT_LENGTH_CHANGE.toString())
				.getChild(MessageFieldType.MESSAGE_LENGTH.toString())
				.addChild(UpdatePattern.CHANGE_FIELD_LENGTH.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.ENCODING_INTEGRATION.toString())
				.getChild(MessageFieldType.ENCODING.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.MULTI_ENCODING_SUPPORT.toString())
				.getChild(MessageFieldType.ENCODING.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_CODE.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_CODE.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_CODE.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_TAG.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_TAG.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_TAG.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString())
				.getChild(RequirementChange.LANGUAGE_SUPPORT_CHANGE.toString())
				.getChild(MessageFieldType.LANGUAGE_TAG_LENGTH.toString())
				.addChild(UpdatePattern.ADD_NEW_FIELD.toString());

		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.SECURITY_REQUIREMENT_CHANGE.toString())
				.getChild(MessageFieldType.CONTROL_FLAG.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());

		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString())
				.getChild(MessageFieldType.CONTROL_FLAG.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString())
				.getChild(MessageFieldType.CONTROL_FLAG.toString())
				.addChild(UpdatePattern.CHANGE_FIELD_LENGTH.toString());

		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.SESSION_MGMT_CHANGE.toString())
				.getChild(MessageFieldType.SESSION_MGMT.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.SESSION_MGMT_CHANGE.toString())
				.getChild(MessageFieldType.SESSION_MGMT.toString()).addChild(UpdatePattern.DELETE_FIELD.toString());
		root.getChild(Functionality.MESSAGE_HANDLING.toString())
				.getChild(RequirementChange.SESSION_MGMT_CHANGE.toString())
				.getChild(MessageFieldType.SESSION_MGMT.toString())
				.addChild(UpdatePattern.CHANGE_FIELD_LENGTH.toString());
	}
}
