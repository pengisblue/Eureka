import React, { useState, useEffect } from "react";
import { StyleSheet, View, Text, Dimensions, Pressable } from "react-native";
import { MaterialCommunityIcons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import { useSelector, useDispatch } from "react-redux";
import TokenUtils from "../../../stores/TokenUtils";
import { getMySingleCardBenefitList } from "../../../apis/ProductApi";
import { selectPayCardBenefit } from "../../../slices/productSlice";

const SCREEN_WIDTH = Dimensions.get("window").width;

function CurrentBenefit() {
  const navigation = useNavigation();
  const [benefitAmount, setBenefitAmount] = useState("");
  const [token, setToken] = useState("");
  const [selectCardUserCardId, setSelectCardUserCardId] = useState("");
  const selectCardInfo = useSelector(
    (state) => state.productList.selectPayCardInfo
  );
  const dispatch = useDispatch();

  useEffect(() => {
    const fetchToken = async () => {
      const accessToken = await TokenUtils.getAccessToken();
      setToken(accessToken);
    };

    fetchToken();
  }, []);

  useEffect(() => {
    if (token && selectCardInfo) {
      setSelectCardUserCardId(selectCardInfo.userCardId);
    } else {
      setBenefitAmount("");
    }
  }, [selectCardInfo, token]);

  useEffect(() => {
    if (token && selectCardInfo) {
      getMySingleCardBenefitList(
        token,
        202403,
        selectCardUserCardId,
        (res) => {
          setBenefitAmount(res.data.totalDiscount);
          dispatch(selectPayCardBenefit(res.data));
        },
        (err) => {
          console.log("CurrentBenefit1111, 현재결제카드혜택불러오기 실패", err);
        }
      );
    }
  }, [token, selectCardUserCardId]);

  return (
    <View>
      <Pressable
        onPress={() => navigation.navigate("CurrentBenefitMore")}
        style={styles.container}
      >
        <Text style={styles.maintext}>현재카드 혜택</Text>
        <View style={styles.rightContainer}>
          <Text style={styles.benefitAmount}>
            {benefitAmount.toLocaleString()}원
          </Text>

          <Pressable onPress={() => navigation.navigate("CurrentBenefitMore")}>
            <MaterialCommunityIcons
              name="chevron-right"
              size={26}
              style={styles.nextBtn}
            />
          </Pressable>
        </View>
      </Pressable>
    </View>
  );
}

export default CurrentBenefit;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: 16,
    backgroundColor: "#e4e4e4",
    width: SCREEN_WIDTH - 30,
    borderRadius: 10,
  },
  maintext: {
    fontSize: 15,
    fontWeight: "200",
  },
  rightContainer: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
  },
  benefitAmount: {
    fontSize: 15,
    marginRight: 10,
  },
  nextBtn: {},
});
