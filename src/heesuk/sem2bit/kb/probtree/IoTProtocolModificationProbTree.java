package heesuk.sem2bit.kb.probtree;

import heesuk.sem2bit.kb.protocol.enums.Functionality;
import heesuk.sem2bit.kb.protocol.enums.MessageFieldType;
import heesuk.sem2bit.kb.protocol.enums.RequirementChange;
import heesuk.sem2bit.kb.protocol.enums.UpdatePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoTProtocolModificationProbTree extends ModificationProbTree{
	private static final Logger LOG = LoggerFactory.getLogger(IoTProtocolModificationProbTree.class);

	public IoTProtocolModificationProbTree(){
		super();
	}

	@Override
	public void addTreeNodes() {
		// Depth 1 -> Depth 2
		root.addChild(Functionality.PROTOCOL_BASIC_INFO.toString());
		root.addChild(Functionality.PROTOCOL_BEHAVIOR.toString());
		root.addChild(Functionality.CONTENT_PARSING.toString());

		// Depth 2 -> Depth 3
		root.getChild(Functionality.PROTOCOL_BASIC_INFO.toString()).addChild(RequirementChange.NOTICE_VERSION_UPDATE.toString());
		root.getChild(Functionality.PROTOCOL_BASIC_INFO.toString()).addChild(RequirementChange.SIMPLIFICATION.toString());

		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).addChild(RequirementChange.BETTER_DESCRIPTION.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).addChild(RequirementChange.CONTROL_OPTION_ADDITION.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).addChild(RequirementChange.SIMPLIFICATION.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString()).addChild(RequirementChange.CONTROL_OPTION_ADDITION.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).addChild(RequirementChange.BETTER_DESCRIPTION.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).addChild(RequirementChange.SIMPLIFICATION.toString());

		// Depth 3 -> Depth 4
		root.getChild(Functionality.PROTOCOL_BASIC_INFO.toString()).
				getChild(RequirementChange.NOTICE_VERSION_UPDATE.toString()).
				addChild(MessageFieldType.VERSION_INFO.toString());
		root.getChild(Functionality.PROTOCOL_BASIC_INFO.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				addChild(MessageFieldType.VERSION_INFO.toString());
		root.getChild(Functionality.PROTOCOL_BASIC_INFO.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				addChild(MessageFieldType.PROTOCOL_NAME.toString());

		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				addChild(MessageFieldType.RETURN_CODE.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				addChild(MessageFieldType.RETURN_CODE.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				addChild(MessageFieldType.OP_CODE.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				addChild(MessageFieldType.CONTROL_FLAG.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				addChild(MessageFieldType.CONNECT_FLAG.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				addChild(MessageFieldType.RETURN_CODE.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				addChild(MessageFieldType.OP_CODE.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				addChild(MessageFieldType.CONTROL_FLAG.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				addChild(MessageFieldType.CONNECT_FLAG.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				addChild(MessageFieldType.MESSAGE_LENGTH.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				addChild(MessageFieldType.OFFSET.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				addChild(MessageFieldType.ENCODING.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				addChild(MessageFieldType.LANGUAGE_CODE.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				addChild(MessageFieldType.MESSAGE_LENGTH.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				addChild(MessageFieldType.OFFSET.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				addChild(MessageFieldType.ENCODING.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				addChild(MessageFieldType.LANGUAGE_CODE.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				addChild(MessageFieldType.MESSAGE_LENGTH.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				addChild(MessageFieldType.OFFSET.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				addChild(MessageFieldType.ENCODING.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				addChild(MessageFieldType.LANGUAGE_CODE.toString());

		// Depth 4 -> Depth 5
		root.getChild(Functionality.PROTOCOL_BASIC_INFO.toString()).
				getChild(RequirementChange.NOTICE_VERSION_UPDATE.toString()).
				getChild(MessageFieldType.VERSION_INFO.toString()).addChild(UpdatePattern.VALUE_CHANGE.toString());
		root.getChild(Functionality.PROTOCOL_BASIC_INFO.toString()).
				getChild(RequirementChange.NOTICE_VERSION_UPDATE.toString()).
				getChild(MessageFieldType.VERSION_INFO.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.PROTOCOL_BASIC_INFO.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				getChild(MessageFieldType.VERSION_INFO.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.PROTOCOL_BASIC_INFO.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				getChild(MessageFieldType.PROTOCOL_NAME.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());

		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				getChild(MessageFieldType.RETURN_CODE.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				getChild(MessageFieldType.RETURN_CODE.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				getChild(MessageFieldType.OP_CODE.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				getChild(MessageFieldType.CONTROL_FLAG.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				getChild(MessageFieldType.CONNECT_FLAG.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				getChild(MessageFieldType.RETURN_CODE.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				getChild(MessageFieldType.OP_CODE.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				getChild(MessageFieldType.CONTROL_FLAG.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.PROTOCOL_BEHAVIOR.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				getChild(MessageFieldType.CONNECT_FLAG.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());

		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				getChild(MessageFieldType.MESSAGE_LENGTH.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				getChild(MessageFieldType.OFFSET.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				getChild(MessageFieldType.OFFSET.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				getChild(MessageFieldType.ENCODING.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				getChild(MessageFieldType.ENCODING.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				getChild(MessageFieldType.LANGUAGE_CODE.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.CONTROL_OPTION_ADDITION.toString()).
				getChild(MessageFieldType.LANGUAGE_CODE.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				getChild(MessageFieldType.MESSAGE_LENGTH.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				getChild(MessageFieldType.OFFSET.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				getChild(MessageFieldType.OFFSET.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				getChild(MessageFieldType.ENCODING.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				getChild(MessageFieldType.ENCODING.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				getChild(MessageFieldType.LANGUAGE_CODE.toString()).addChild(UpdatePattern.ADD_NEW_FIELD.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.BETTER_DESCRIPTION.toString()).
				getChild(MessageFieldType.LANGUAGE_CODE.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				getChild(MessageFieldType.MESSAGE_LENGTH.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				getChild(MessageFieldType.OFFSET.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				getChild(MessageFieldType.ENCODING.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
		root.getChild(Functionality.CONTENT_PARSING.toString()).
				getChild(RequirementChange.SIMPLIFICATION.toString()).
				getChild(MessageFieldType.LANGUAGE_CODE.toString()).addChild(UpdatePattern.CHANGE_VOCA.toString());
	}
}
