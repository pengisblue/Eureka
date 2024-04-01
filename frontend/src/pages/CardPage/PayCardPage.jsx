import { FlatList, StyleSheet, Text, View, Image, TouchableOpacity, Pressable } from "react-native"
import React, { useState, useEffect } from "react";
import { useNavigation, useFocusEffect } from '@react-navigation/native';
import { getPayCard } from "../../apis/CardAPi";
import TokenUtils from '../../stores/TokenUtils';


function PayCardPage() {
  const navigation = useNavigation()
  const [token, setToken] = useState('');
  const [cardList, setCardList] = useState([]);
  const currentDate = new Date();
  const currentMonth = ('0' + (currentDate.getMonth() + 1)).slice(-2); 
  const currentYear = currentDate.getFullYear().toString()

  useEffect(() => {
    const fetchToken = async () => {
      const accessToken = await TokenUtils.getAccessToken();
      setToken(accessToken);
    };
  
    fetchToken();
  }, []);
  
  const fetchCardList = async () => {
    if (token) { 
      getPayCard(
        token,
        currentYear+currentMonth,
        (res) => {
          setCardList(res.data);
        },
        (err) => console.log(err)
      );
    }
  };
  
  useFocusEffect(
    React.useCallback(() => {
      fetchCardList();
      return () => {};
    }, [token])
  );


  return (
    <View style={{backgroundColor:'#ffffff'}}>
      <FlatList
        style={styles.listStyle}
        data={cardList}
        keyExtractor={(item, index) => index.toString()}
        renderItem={({ item }) => (
          <View style={styles.cardContainer}>
            <Image
              source={{ uri: item.imagePath }}
              style={item.imgAttr === 1 ? [styles.cardImage, styles.rotatedImage] : styles.cardImage}
            />
            <View style={{ flex: 1 }}>
              <Text style={styles.cardTitle}>{item.cardName}</Text>
              <Text>{item.firstCardNumber}- **** - **** - {item.lastCardNumber}</Text>
              {item.previousPerformance - item.totalAmt > 0 ? (
                <Text>
                  이용 실적이 
                  <Text style={styles.highlightText}> {(item.previousPerformance - item.totalAmt).toLocaleString()}원 </Text>
                  남았어요
                </Text>
              ) : (
                <Text style={styles.achievementText}>카드 실적을 달성하였어요!</Text>
              )}
            </View>
          </View>
        )}
      />
      <Pressable onPress={()=>navigation.navigate('PayCardEnroll')}>
        <View style={styles.btnContainer}>
          <Image source={require('../../../assets/HomeIcon/Plus.png')} style={styles.img}/>
          <Text style={{ fontSize: 20, color: '#0050FF' }}>결제 카드 등록하기</ Text>
        </View>
      </Pressable>
    </View>
  )
}

export default PayCardPage

const styles = StyleSheet.create({
  cardContainer: {
    justifyContent:'space-around',
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 8,
    padding: 10,
    paddingBottom: 20,
  },
  listStyle: {
    height: 450,
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 3,
  },
  cardImage: {
    width: 102,
    height: 64,
    marginRight: 20,
    borderRadius: 8,
  },
  rotatedImage: {
    transform: [{ rotate: '-90deg' }],
    width: 64,
    height: 102,
    marginHorizontal: 20,
    marginEnd: 35,
    marginVertical: -15
  },
  highlightText: {
    color: '#007bff', // 파란색으로 하이라이트
  },
  remainingText: {
    color: '#007bff', // 파란색
  },
  achievementText: {
    color: 'green', // 달성시 색상, 예시로 녹색을 사용했습니다. 원하는 색상으로 변경 가능
  },
  btnContainer: {
    flexDirection: 'row',
    alignItems:'center',
    height: 150,
    width: 300,
    margin:20,
    backgroundColor: '#F3F3F3',
    marginBottom: 100,
    borderRadius: 20,
    justifyContent: 'center',
    elevation: 5
  },
  img: {
    height: 40,
    width: 40,
    marginEnd: 10,
  }
})
