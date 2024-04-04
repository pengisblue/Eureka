import { StyleSheet, View, Text, Image } from "react-native";
import { getMyTop3Cards } from "../../../../apis/StatisticsApi";
import TokenUtils from "../../../../stores/TokenUtils";
import { useEffect, useState } from "react";

function CardRakingList() {
  const [token, setToken] = useState("");
  const [top3CardList, setTop3CardList] = useState([]);

  useEffect(() => {
    const fetchToken = async () => {
      const accessToken = await TokenUtils.getAccessToken();
      setToken(accessToken);
    };
    fetchToken();
  }, []);

  useEffect(() => {
    if (token) {
      const getCurrentDate = () => {
        const date = new Date();
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        return year * 100 + month;
      };
      const currentDate = 202403;

      getMyTop3Cards(
        token,
        currentDate,
        (res) => {
          setTop3CardList(res.data.bestCardStatisticsList);
        },
        (err) => {
          console.log(err, "cardRanking err");
        }
      );
    }
  }, [token]);

  function getRotationStyle(imageAttribute) {
    // imageAttribute가 1이면 이미지를 90도 회전시키는 스타일 반환
    if (imageAttribute === 1) {
      return {
        transform: [{ rotate: "90deg" }],
        width: 95,
        height: 155,
        resizeMode: "contain",
        marginLeft: 30,
      };
    } else {
      // 그렇지 않으면, 빈 스타일 객체 반환
      return {};
    }
  }

  return (
    <View style={styles.container}>
      <View style={styles.topCard}>
        <View style={styles.imageContainer}>
          <Image
            source={require("../../../../../assets/1등뱃지.png")}
            style={styles.oneBadge}
          />
          <Image
            source={{ uri: top3CardList[0]?.imagePath }}
            style={[
              styles.image,
              getRotationStyle(top3CardList[0]?.imageAttribute),
            ]}
          ></Image>
          <Text style={{ marginTop: 10 }}>{top3CardList[0]?.cardName}</Text>
        </View>

        <View style={styles.topCardText}>
          <Text style={{ fontSize: 15, fontWeight: "medium" }}>
            약{" "}
            <Text
              style={{
                color: "#5574ff",
                fontSize: 18,
                fontWeight: "semibold",
              }}
            >
              {top3CardList[0]?.totalDiscount.toLocaleString()}원
            </Text>
            의
          </Text>
          <Text>혜택을 받았어요!</Text>
        </View>
      </View>

      <View style={styles.bottomCard}>
        <View style={styles.imageContainer2}>
          <Image
            source={require("../../../../../assets/2등뱃지.png")}
            style={styles.otherBadge}
          />
          <Image
            source={{ uri: top3CardList[1]?.imagePath }}
            style={[
              styles.image,
              getRotationStyle(top3CardList[1]?.imageAttribute),
            ]}
          ></Image>
          <View style={styles.otherCardInfo}>
            <Text style={styles.otherCardName}>
              {top3CardList[1]?.cardName}
            </Text>
            <Text style={styles.otherCardAmount}>
              약 {top3CardList[1]?.totalDiscount.toLocaleString()}원
            </Text>
          </View>
        </View>

        <View style={styles.imageContainer2}>
          <Image
            source={require("../../../../../assets/3등뱃지.png")}
            style={styles.otherBadge}
          />
          <Image
            source={{ uri: top3CardList[2]?.imagePath }}
            style={[
              styles.image,
              getRotationStyle(top3CardList[2]?.imageAttribute),
            ]}
          ></Image>
          <View style={styles.otherCardInfo}>
            <Text style={styles.otherCardName}>
              {top3CardList[2]?.cardName}
            </Text>
            {top3CardList[2]?.totalDiscount === 0 ? (
              <Text style={styles.otherCardAmount}>0원</Text>
            ) : (
              <Text style={styles.otherCardAmount}>
                약 {top3CardList[2]?.totalDiscount.toLocaleString()}원
              </Text>
            )}
          </View>
        </View>
      </View>
    </View>
  );
}

export default CardRakingList;

const styles = StyleSheet.create({
  container: {
    justifyContent: "center",
    alignItems: "center",
  },
  topCard: {},
  imageContainer: {
    position: "relative",
    justifyContent: "center",
    alignItems: "center",
  },
  imageContainer2: {
    marginTop: 15,
    flexDirection: "row",
    alignItems: "center",
    margin: 5,
  },
  topCardText: {
    marginTop: 20,
    justifyContent: "center",
    alignItems: "center",
  },
  bottomCard: {
    marginTop: 20,
  },
  otherCardInfo: {
    flexDirection: "column",
    marginLeft: 24,
    justifyContent: "center",
    alignItems: "flex-end",
    width: 120,
  },
  // 변경된 스타일 예시
  image: {
    height: 95, // 높이를 유지
    width: 155, // 너비를 유지
    resizeMode: "contain", // 이미지가 컨테이너에 맞게 조정되도록 설정
  },
  image2: {
    marginTop: 30,
    height: 80, // 높이를 유지
    width: 130, // 너비를 유지
    resizeMode: "contain", // 이미지가 컨테이너에 맞게 조정되도록 설정
  },
  otherCardName: {
    fontSize: 13,
  },
  otherCardAmount: {
    fontSize: 12,
    color: "#3482ff",
  },
  oneBadge: {
    position: "absolute",
    top: -20,
    left: -20,
    zIndex: 1,
    height: 60,
    width: 50,
  },
  otherBadge: {
    position: "absolute",
    top: -15,
    left: -20,
    zIndex: 1,
    height: 60,
    width: 50,
  },
});
